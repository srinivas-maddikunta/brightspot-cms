Query Relationships
-------------------

Dari supports querying relationships using path notation, i.e. field/subfield, in `WHERE` clauses. Use this query to find all articles by a particular author, for example. The following models demonstrate how to use path notation.

.. code-block:: java

    public class Article extends Content {
        @Index private Author author;
        private String title;
        private String body;

        // Getters and Setters...
    }

    public class Author extends Content {
        private String firstName;
        @Index private String lastName;
        @Index private String email;

        // Getters and Setters...
    }

There are two ways to find articles by a specific author. You can query for the author first and then query for articles by that author.

.. code-block:: java

    Author author = Query.from(Author.class).where("email = 'john.smith@psddev.com'");
    List<Articles> = Query.from(Article.class).where("author = ?", author);

Or you may find it easier and more efficient to do this in a single query using path notation.

.. code-block:: java

    List<Articles> = Query.from(Article.class).where("author/email = 'john.smith@psddev.com'");