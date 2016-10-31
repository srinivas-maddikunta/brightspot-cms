Paginated Results
-----------------

Brightspot supports the ability to return paginated results. You can specify an offset for a list of items, instead of selecting all of them, by using the `PaginatedResult` class.

.. code-block:: java

    int limit = 100;
    
    Query<Content> query = Query.from(Content.class);
        PaginatedResult<Content> result = query.select(0, limit);
        while (result.hasNext()) {
            //do something with the result object
            result = query.select(result.getNextOffset(), limit);
		}

Use the method ``getItems()`` when rendering this result set to return the list, and ``hasNext()``, ``hasPrevious``, or ``getNextOffset()`` to control pagination.
    
See the `JavaDocs <https://artifactory.psdops.com/psddev-releases/com/psddev/dari/3.2.2188-2d7dae/dari-3.2.2188-2d7dae-javadoc.jar!/index.html>`_ for a full list of available methods.