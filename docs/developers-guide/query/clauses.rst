Clauses
---------

Brightspot lets you retrieve content by providing a database-abstraction API through Dari. Queries are represented by instances of the Dari Query class. With Dari installed, the Code Tool can be used to test out these queries.

The FROM Clause
~~~~~~~~~~~~~~~~

The simplest query is to select all records of a given type:

.. code-block:: java

    List<Author> authors = Query.from(Author.class).selectAll();

This will return all instances of the `Author` class.

Inheritance also works with the `FROM` clause by querying from a base class. For example, to build an activity feed:

.. code-block:: java
    :linenos:

    public class Activity extends Content {
        @Index private Date activityDate;
        @Index private User user;
    }

    public class Checkin extends Activity { ... }
    public class Comment extends Activity { ... }
    public class ReadArticle extends Activity { ... }
    public class PostedArticle extends Activity { ... }


Given this class hierarchy, you can query for activity by querying from the `Activity` class. This query will also retrieve any records that are subclasses of `Activity`.

.. code-block:: java
    :linenos:

    PaginatedResult<Activity> results = Query.from(Activity.class).
        where("user = ?", user).
        sortDescending("activityDate").
        select(0, 10);

The LIMIT Clause
~~~~~~~~~~~~~~~~~~~~

Dari supports limiting the number of results returned.

.. code-block:: java
    :linenos:

    PaginatedResult<Article> articles = Query.from(Article.class).
        sortAscending("title").
        select(1000, 10);
    List<Article> items = articles.getItems();

This example will start at offset 1000 and return the next 10 instances of `Article`. The object returned from a limit query is a `PaginatedResult`, a pagination helper class that provides efficient methods for building pagination, such as `hasNext()` and `getNextOffset()`.

The WHERE Clause
~~~~~~~~~~~~~~~~

The `WHERE` method allows you to filter which object instances are returned. In order to use a field in a `WHERE` clause, it must have the @Index annotation.

.. code-block:: java

    Author author = Query.from(Author.class).where("name = 'John Smith'").first();

This exmaple will return the first instance of `Author` with the name 'John Smith'.

Logical operations `not`, `or`, `and` are supported. For a full list of supported predicates, see :doc:`predicates`.


.. code-block:: java
    :linenos:

    List<Author> authors = Query.from(Author.class).
        where("name = 'John Smith' or name = 'Jane Doe'").
        selectAll();

The `Query` class follows the builder pattern so this query can also be written as:

.. code-block:: java
    :linenos:

    List<Author> authors = Query.from(Author.class).
        where("name = 'John Smith'").
        and("name = 'Jane Doe'").
        selectAll();

The ORDER BY Clause
~~~~~~~~~~~~~~~~~~~~~

You can order results using `sortAscending` and `sortDescending`. Both of these methods take the name of the field to sort. The sorted field must have the `@Indexed` annotation.

.. code-block:: java

    List<Author> authors = Query.from(Author.class).sortAscending("name").first();

or

.. code-block:: java

    List<Author> authors = Query.from(Author.class).sortAscending("name").selectAll();


or

.. code-block:: java

    List<Author> authors = Query.from(Author.class).sortAscending("name").select(offset, limit).getItems();

The GROUP BY Clause
~~~~~~~~~~~~~~~~~~~~~

Using the `groupBy` method allows queries to return items in groupings based on associations. The example below returns a count of articles grouped by the tags associated with each.

.. code-block:: java
    :linenos:

    public class Article extends Content {
        private Tag tag;
        private String author;

        // Getters and Setters
    }

The groupBy query:

.. code-block:: java
    :linenos:

    List<Grouping<Article>> groupings = Query.from(Article.class).groupBy("tag")

    for (Grouping grouping : groupings) {
        Tag tag = (Tag) grouping.getKeys().get(0);
        long count = grouping.getCount();
    }

You can retrieve the items that make up a grouping by using the `createItemsQuery` method on the returned `Grouping` objects. This method will return a `Query` object.

.. code-block:: java

    List<Grouping<Article>> groupings = Query.from(Article.class).groupBy("tag")

    for (Grouping grouping : groupings) {
        Tag tag = (Tag) grouping.getKeys().get(0);
        List<Article> articles = grouping.createItemsQuery().selectAll();
    }  
  
You can also group by more than one item, for example, a Tag and Author.

.. code-block:: java

    List<Grouping<Article>> groupings = Query.from(Article.class).groupBy("tag", "author");

    for (Grouping grouping : groupings) {
        Tag tag = (Tag) grouping.getKeys().get(0);
        Author author = (Author) grouping.getKeys().get(1);
        long count = grouping.getCount();
    }

Sort the count using the `_count`:

.. code-block:: java

    List<Grouping<Article>> groupings = Query.from(Article.class).sortAscending("_count").groupBy("tag");

    for (Grouping grouping : groupings) {
        Tag tag = (Tag) grouping.getKeys().get(0);
        List<article> articles = grouping.createItemsQuery().getSorters().clear().selectAll();
    }

The FIELDS Clause
~~~~~~~~~~~~~~~~~~~~~

Specific fields from in a Dari object can be returned for a given object. Other fields will return null accessed. The `.fields` clause requires a MySQL Plugin to be installed.

.. code-block:: java

    Author author = Query.from(Author.class).fields("name", "age").selectAll();

