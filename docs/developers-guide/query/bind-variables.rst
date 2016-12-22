Bind Variables
--------------

In :doc:`querying-relationships`, the "?" (question mark) was used in the WHERE clause when specifying the author. Dari supports bind variables in query strings using "?"" for placeholders.

.. code-block:: java
    :linenos:

    String authorName = "John Smith";
    Author author = Query.from(Author.class).
        where("name = ?", authorName).
        first();

Placeholders can be basic types like String, or Integer, but they can also be Lists, or other Dari objects, allowing IN style queries.

.. code-block:: java
    :linenos:

    List<String> names = new ArrayList<String>();
    names.add("John Smith");
    names.add("Jane Doe");
    List<Author> authors = Query.from(Author.class).
        where("name = ?", names).
        selectAll();