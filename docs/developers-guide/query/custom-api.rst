Custom API
----------

Create a custom API by extending the `HttpServlet` class, and using the `@RoutingFilter.Path()` annotation to create the accessible URL for the API.

Use the Dari query API to construct the results to be made available via the API, or create a tailored output. The example below uses the `ObjectUtils.toJson` to create an API feed for all articles sorted by creation date, showing their name, body text, and URL. The Dari Query API is used to construct the list.

Article Object:

.. code-block:: java

	public class Article extends Content {

		private String headline;
		private ReferentialText body;
		
		// Getters and Setters
	

Servlet Example:

.. code-block:: java

	@RoutingFilter.Path(value = "/api-feed")
	public class FeedServlet extends HttpServlet {

		@Override
		protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

			List<Map<String, Object>> articleFeed = new ArrayList<Map<String, Object>>();

				List<Article> articles = Query.from(Article.class).where(Content.PUBLISH_DATE_FIELD + " != missing").sortDescending(Content.PUBLISH_DATE_FIELD).select(0, 100).getItems();
				for (Article article : articles) {
					Map<String, Object> articleJson = new HashMap<String, Object>();
					articleJson.put("title", article.getHeadline());
					articleJson.put("body", article.getBody());
					articleJson.put("link", article.getPermalink());

					articleFeed.add(articleJson);
				}

				response.setContentType("application/json");
				response.getWriter().write(ObjectUtils.toJson(articleFeed));
		}
	}

You can also create full object API feeds:

.. code-block:: java

	@RoutingFilter.Path(value = "/api-feed")
	public class QueryFeedServlet extends HttpServlet {

		protected String getPermissionId() {
			return null;
		}

		@Override
		protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
			
			List<Product> products = Query.from(Product.class).selectAll();

				response.getWriter().write(ObjectUtils.toJson(products));
				}

	}



