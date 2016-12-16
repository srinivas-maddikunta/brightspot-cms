Sitemap
-------

Construct sitemaps in Brightspot using three classes: a Filter to provide access to the sitemap, a Task to update the sitemap periodically, and a utility to generate the sitemap automatically.

SiteMap Filter
~~~~~~~~~~~~~~

Create the /sitemap.xml filter on the site that can be accessed.

.. code-block:: java

    public class SiteMapFilter extends AbstractFilter {
        private static final Logger logger = LoggerFactory.getLogger(SiteMapFilter.class);

        @Override
        protected void doRequest(HttpServletRequest request, HttpServletResponse response,
                FilterChain chain) throws IOException, ServletException {

            if (request.getServletPath().startsWith("/sitemap")) {
                response.setContentType("text/xml");
                String path = request.getServletPath().substring(1);
                Text text = Query.findUnique(Text.class, "name", path);

                if (text != null) {
                    try {
                        PrintWriter writer = response.getWriter();
                        writer.write(text.getText());
                        writer.close();
                    } catch (IOException e) {
                        logger.error("IO exception while writing out sitemap xml to response", e);
                        try {
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        } catch (IOException errorException) {
                            logger.error("IO exception while sending internal server error response", errorException);
                        }
                    }
                } else {
                    logger.error("No sitemap found");
                    try {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    } catch (IOException errorException) {
                        logger.error("IO exception while sending page not found response", errorException);
                    }
                }
            } else {
                chain.doFilter(request, response);
            }
        }
    }

SiteMap Task
~~~~~~~~~~~~

Generate a sitemap linked to either a manual trigger or scheduled job.

.. code-block:: java

    public class SiteMapTask implements ServletContextListener{

        private static final String LOCALHOST_IDENTIFIER = "localhost";
        private static final long NUM_SECONDS_IN_A_DAY = 24 * 60 * 60;
        private static final int START_HOUR = 1;
        private static final long IMMEDIATE_BUILD_DELAY = 60;

        private static final Logger logger = LoggerFactory.getLogger(SiteMapTask.class);

        @Override
        public void contextInitialized(ServletContextEvent sce) {

            if (isSiteMapTaskEnabled()) {
                long initialDelayInSeconds;

                if (isSiteMapMissing()) {
                    logger.info("No sitemap exists");
                    initialDelayInSeconds = IMMEDIATE_BUILD_DELAY;
                } else {
                    logger.info("Scheduling sitemap task to start at " + START_HOUR + " hrs");

                    Calendar currentTime = Calendar.getInstance();

                    Calendar startTime = (Calendar) currentTime.clone();
                    startTime.set(Calendar.HOUR_OF_DAY, START_HOUR);
                    startTime.set(Calendar.MINUTE, 0);

                    initialDelayInSeconds = (startTime.getTimeInMillis() - currentTime.getTimeInMillis()) / 1000l;
                    if (initialDelayInSeconds &lt; 0) {
                        // Missed it today, so schedule for tomorrow
                        initialDelayInSeconds += NUM_SECONDS_IN_A_DAY;
                    }

                    if (initialDelayInSeconds &lt; 0) {
                        logger.error("Calculated initial delay of " + initialDelayInSeconds + " for sitemap task.  Will build immediately.");
                        initialDelayInSeconds = IMMEDIATE_BUILD_DELAY;
                    }
                }

                logger.info("Starting SiteMap Task in " + initialDelayInSeconds + " seconds");

                Task task = new InternalTask();
                task.scheduleAtFixedRate(initialDelayInSeconds, NUM_SECONDS_IN_A_DAY);
            } else {
                logger.info("Not scheduling sitemap task on this host");
            }
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
        }

        private boolean isSiteMapTaskEnabled() {
            SiteSettings siteSettings = Utils.getSiteSettings();
            if (siteSettings == null) {
                logger.error("Cannot retrieve site settings");
                return false;
            }

            if (siteSettings.getSiteMapSettings() == null) {
                logger.error("Cannot retrieve site map settings");
                return false;
            }

            String thisHost = null;
            try {
                thisHost = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                logger.error("Exception retrieving host name");
                return false;
            }
            if (thisHost == null || thisHost.length() == 0) {
                logger.error("Null host name");
                return false;
            }

            String jobHost = siteSettings.getSiteMapSettings().getJobHost();
            if (jobHost == null || jobHost.length() == 0) {
                logger.error("No job host defined");
                return false;
            }

            return (jobHost.equals(thisHost) || jobHost.equals(LOCALHOST_IDENTIFIER));
        }

        private boolean isSiteMapMissing() {
            try {
                Text siteMapText = Query.findUnique(Text.class, "name", "sitemap.xml");
                return siteMapText == null;
            } catch (Exception e) {
                logger.error("Exception while checking if site map is missing.  Assuming it is there.", e);
                return false;
            }
        }

        private class InternalTask extends Task {
            @Override
            protected void doTask() throws Exception {
                SiteMapUtil.buildSiteMap();
            };
        }
    }

SiteMap Util
~~~~~~~~~~~~

Construct the query to generate the sitemap.

.. code-block:: java

    public class SiteMapUtil {

        private static final int MAX_ITEMS_PER_SITEMAP = 50000;
        private static final int PAGE_SIZE = 100;
        private static final String DOMAIN = "www.yoursite.com";

        private static Logger logger = LoggerFactory.getLogger(SiteMapUtil.class);
        private static DateTimeFormatter dtf = DateTimeFormat.forPattern("YY-MM-dd");

        /**
        * Build site maps
        * @return number of site map files
        */

        public static void buildSiteMap() {
            logger.info("Building site map");

            List<Stringbuilder> siteMapList = new ArrayList<Stringbuilder>();

            // Build site maps
            int currentPageIndex = -1;
            int currentPageItemCount = 0;
            Query<Content> query = Query.from(Content.class)
                .where(Directory.Static.hasPathPredicate())
                .and("_type != ?", ObjectType.getInstance(ErrorPage.class)) // exclude 404 &amp; 500 published error pages
                ;

            for (Content content : query.iterable(PAGE_SIZE)) {
                if (currentPageItemCount == 0) {
                    // Start new site map
                    StringBuilder siteMap = new StringBuilder();
                    siteMap.append("<!--?xml version=\"1.0\" encoding=\"UTF-8\"?-->");
                    siteMap.append("<urlset xmlns="\"http://www.sitemaps.org/schemas/sitemap/0.9\"">");
                    siteMapList.add(siteMap);
                    currentPageIndex++;
                }

                siteMapList.get(currentPageIndex).append(getContentNode(content));
                currentPageItemCount++;
                if (currentPageItemCount &gt;= MAX_ITEMS_PER_SITEMAP) {
                    currentPageItemCount = 0;
                }
            }

            for (StringBuilder siteMap : siteMapList) {
                siteMap.append("</urlset>");
            }

            // Save site map files
            for (int siteMapIndex = 0; siteMapIndex &lt; siteMapList.size(); siteMapIndex++) {
                String siteMapName = "sitemap-" + siteMapIndex + ".xml";
                Text siteMapText = Query.findUnique(Text.class, "name", siteMapName);
                if (siteMapText == null) {
                    siteMapText = new Text();
                    siteMapText.setName(siteMapName);
                }
                siteMapText.setText(siteMapList.get(siteMapIndex).toString());
                siteMapText.save();
            }

            // Build site map index
            StringBuilder mainSiteMap = new StringBuilder();
            mainSiteMap.append("<!--?xml version=\"1.0\" encoding=\"UTF-8\"?-->");
            mainSiteMap.append("<sitemapindex xmlns="\"http://www.sitemaps.org/schemas/sitemap/0.9\"">");
            for (int siteMapIndex = 0; siteMapIndex &lt; siteMapList.size(); siteMapIndex++) {
                mainSiteMap.append(getSiteMapNode(siteMapIndex));
            }
            mainSiteMap.append("</sitemapindex>");

            // Save site map index
            String siteMapName = "sitemap.xml";
            Text mainSiteMapText = Query.findUnique(Text.class,"name",siteMapName);
            if (mainSiteMapText == null) {
                mainSiteMapText = new Text();
                mainSiteMapText.setName(siteMapName);
            }
            mainSiteMapText.setText(mainSiteMap.toString());
            mainSiteMapText.save();

            logger.info("Site map completed");
        }

        private static String getSiteMapNode(int siteMapIndex) {
            StringBuilder siteMapNode = new StringBuilder();
            siteMapNode.append("<sitemap>")
                        .append("<loc>http://")
                            .append(DOMAIN)
                            .append("/sitemap-")
                            .append(Integer.toString(siteMapIndex))
                            .append(".xml")
                        .append("</loc>")
                        .append("<lastmod>")
                            .append(dtf.print((new DateTime())))
                        .append("</lastmod>")
                    .append("</sitemap>");

            return siteMapNode.toString();
        }

        private static String getContentNode(Content content) {

            String permalink = content.getPermalink();

            if (permalink == null) {
                logger.error("Null permalink in site map file: this should never happen");
                return "";
            }

            String url = "http://" + DOMAIN + permalink;
            Date date = content.getUpdateDate();

            StringBuilder contentNode = new StringBuilder();

            contentNode.append("<url>")
                    .append("<loc>")
                    .append(url)
                    .append("</loc>");

            if (date != null) {
                contentNode.append("<lastmod>")
                        .append(dtf.print(date.getTime()))
                        .append("</lastmod>");
            }

            contentNode.append("<changefreq>daily</changefreq>");
            //contentNode.append("<priority>0.7</priority>"); //this is where priority would go if required
            contentNode.append("</url>");

            return contentNode.toString();
        }
    }
