Timeouts
--------

You can set a specific timeout length for a query in milliseconds.

.. code-block:: java

    Query.from(Author.class).sortAscending(&quot;name&quot;).timeout(1000.0).selectAll();