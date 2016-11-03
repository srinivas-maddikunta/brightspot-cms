Site Search
-----------

The SearchQueryBuilder object provides search functionality in Brightspot. the EditorialSearchSettings object can also be used for an added editorial component, for example, allowing you to control various parts of the search itself.

For the purpose of the example, the following business rules apply:

* search content types Articles, Images, and Video
* rank Article types to have more weight
* control stop words, synonyms, and the number of search results returned
\

.. code-block:: java

    public class SiteSearch extends EditorialSearchSettings implements Singleton, Directory.Item {

        private int numSearchResults;

        //where searchString is the text the user has entered into the search
        private transient String searchString;

        public SiteSearch() {
            numSearchResults = 20;
            setStopWords(new SearchQueryBuilder.StopWords());
        }

        public Query<?> getQuery() {
            //initialize the query for search
            SearchQueryBuilder searchQuery = new SearchQueryBuilder();

            //set the types that you will search against
            Set<ObjectType> types = new HashSet<>();
            types.add(ObjectType.getInstance(Article.class));
            types.add(ObjectType.getInstance(Video.class));
            types.add(ObjectType.getInstance(Image.class));
            searchQuery.setTypes(types);

            //set the basic rules, followed by the rules set by editors
            searchQuery.onlyReturnPathedContent(true);
            searchQuery.addRules(getEditorialRules());

            //add the other search rules (boost represents powers of 10)
            searchQuery.addRule(new SearchQueryBuilder.BoostType(3, ObjectType.getInstance(Article.class)));

            return searchQuery.toQuery(null, searchString).sortRelevant(100000.0, "_any matchesAll ?", searchString);
        }

        public PaginatedResult<?> getSearchResults(long offset) {
            return getQuery().select(offset, numSearchResults);
        }

        @Override
        public String createPermalink(Site site) {
            return "/search";
        }
    }
