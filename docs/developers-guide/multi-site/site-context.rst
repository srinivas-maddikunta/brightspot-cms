Site Context
------------

Once you've implemented MultiSite, queries that were not considering the current site must be updated. For example, a MostPopularModule:

.. code-block:: java

    public class MostPopularModule extends Content {

        private String name;

        // Without MuliSite
        public PaginatedResult<Article> getPopularContent(){
            Query.from(Article.class).sortDescending("analytics.views").select(0,8);
        }

        // With MultiSite
        public PaginatedResult<Article> getPopularContent(Site site){
            Query query = Query.from(Article.class);
            if (site != null) {
                query.where(site.itemsPredicate());
            }
            return query.sortDescending("analytics.views").select(0,8);
        }

    }

The site must also be checked in the rendering file.

.. code-block:: jsp

    <c:forEach items="${content.getPopularContent(site).getItems()}" var="item" >
        ${item.headline}
    </c:forEach>