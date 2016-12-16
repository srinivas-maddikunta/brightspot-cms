Repeating Tasks
---------------

With tasks, you can take in an API feed and save the data from the feed as one or more objects in Brightspot. In the following examples, blog posts will be ingested into Brightspot using an RSS feed every 5 minutes, so a BlogIngester class needs to be created.

.. code-block:: java

    public class BlogIngester extends RepeatingTask {

    }

The class should extend RepeatingTask, and implement these methods: calculateRunTime and doRepeatingTask. Set the run time and the frequency of the task in the calculateRunTime method. The doRepeatingTask method is where the actual ingestion logic should be.

This example shows the parsing for an RSS feed, but other APIs have specific methodologies for parsing.

.. code-block:: java

    public class BlogIngester extends RepeatingTask {

        public DateTime calculateRunTime(DateTime time){
            //The task should run every 5 minutes
            return every(DateTime.now(), DateTimeFieldType.minuteOfDay(), 0, 5);
        }

        @Override
        protected void doRepeatingTask(DateTime runTime) throws Exception {

            //The API parsing logic should go here. For this example it is an XML feed.
            XmlReader reader = null;
            SyndFeed feed = null;
            String rssUrl = getRss().getUrl();
            if (StringUtils.isBlank(rssUrl)) {
                return;
            }
            try {
                reader = new XmlReader(new URL(rssUrl));
                feed = new SyndFeedInput().build(reader);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }

            int count;
            Iterator i;
            List<BlogPost> posts = new ArrayList<BlogPost>();
            for (count = 0, i = feed.getEntries().iterator(); count < 5 && i.hasNext(); count++) {
                SyndEntry entry = (SyndEntry) i.next();


                BlogPost post = new BlogPost();

                // Save the Title
                Title title = new Title();
                title.setDesktopTitle(entry.getTitle());
                post.setTitle(title);

                // Save the Author
                //post.setAuthorName(entry.getAuthor());

                // Save the Description
                SyndContent description = entry.getDescription();
                String htmlString = description.getValue();
                String plainSummary = Utils.toPlainText(htmlString);
                post.setPlainBody(plainSummary);

                // Save the Image
                post.setImageFromSyndEntry(entry);


                // Publish Date
                post.setPublishedDate(entry.getPublishedDate());

                posts.add(post);
            }

    }
    
Since this ingestion task was created extending RepeatingTask, it gets registered in /_debug/task. Monitor the status of the ingestion task using this tool. The task can also be paused, started, or stopped.