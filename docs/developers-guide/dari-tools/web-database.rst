Web Database
------------

Access data from other instances of Brightspot with the the Web Database tool. From the code tool, you can query objects that exist in other Brightspot instances. The return of Article objects from another instance is shown below: 

.. code-block:: java

    WebDatabase web = new WebDatabase();
    web.setRemoteDatabase("databaseName");
    web.setRemoteUsername("authUsername");
    web.setRemotePassword("authPassword");
    web.setRemoteUrl(http://localhost:8080/_debug/db-web);
    return Query.from(Article.class).where.("title startsWith foo").using(web).select(0,10);

You can also move objects from one database to the other. Once returned, the required Type Id is set. This works for when instances of objects are to be moved into the same model. Below is an example of objects moving from a `dev` to a `qa` instance of a site:

.. code-block:: java

    WebDatabase web = new WebDatabase();
    web.setRemoteDatabase("databaseName");
    web.setRemoteUsername("authUsername");
    web.setRemotePassword("authPassword");
    web.setRemoteUrl("http://localhost:8080/_debug/db-web");
    Article article = Query.from(Article.class).where("_id = ID_HERE").using(web).first();
    article.getState().setDatabase(Database.Static.getDefault()); 
    State articleState = article.getState();
    ObjectType objectType = ObjectType.getInstance(Article.class);
    articleState.setTypeId(objectType.getId());
    articleState.save();   
    return article;

Just as APIs can be ingested into Brightspot, objects in Brightspot can also be exported using the Dari API tool, DB-Web found in `/_debug/db-web/`. To begin, create a query using the `/_debug/code/` tool and provide the query group data to the DB-Web tool.\

Build the Query
~~~~~~~~~~~~~~~~~

Create the query for the API in the /_debug/code tool.\

An example query for articles in the database:\

.. code-block:: java

    public class Code {
        public static Object main() throws Throwable {
            return ObjectUtils.toJson(Query.from(Article.class).getState().getSimpleValues());
        }
    }



The getSimpleValues() method returns a map of all values converted to only simple types. The getState() method returns the state of the object being queried.\

Click the Run button to run the query, the results should display in the Result section on the right side of the page. Copy the group data between the braces:\


"group":"yourGroupID.yourProject.Article","_id":"00000146-a62c-dcb0-a1f6-ee2f0d360000","_type":"00000144-98c9-dc39-a344-dec99d4e0012"


Run the Query
~~~~~~~~~~~~~~~

Go to db-web tool located here /_debug/db-web, and in the URL string append the group data as such:\

/_debug/db-web?action=readFirst&query=\{"group data"\}

Other action operation options include:

/_debug/db-web?action=readPartial&query=\{"group data"\}

/_debug/db-web?action=readPartial&offset="offset number"&query=\{"group data"\}

/_debug/db-web?action=readAll&query=\{"group data"\}

/_debug/db-web?action=readCount&query=\{"group data"\}\

/_debug/db-web?action=readLastUpdate&query=\{"group data"\}}