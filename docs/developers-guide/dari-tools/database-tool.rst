Access data from other instances of Brighspot with the The Web Database tool. From the code tool, you can query objects that exist in other Brightspot instances. The return of Article objects from another instance is shown below:

.. code-block:: java

    WebDatabase web = new WebDatabase();
    web.setRemoteDatabase("databaseName");
    web.setRemoteUsername("authUsername");
    web.setRemotePassword("authPassword");
    web.setRemoteUrl(http://localhost:8080/_debug/db-web);
    return Query.from(Article.class).where.("title startsWith foo").using(web).select(0,10);

You can also move objects from one database to the other. Once returned, the required Type Id is set. This works for when instances of objects are to be moved into the same model. Below is an example of objects moving from a dev to a qa instance of a site:

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