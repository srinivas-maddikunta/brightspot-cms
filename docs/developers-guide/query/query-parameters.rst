Query parameters
----------------

There are a number of query parameters available when working with Brightspot and Dari.

Cache
~~~~~

::

    http://yoursite.com/about-us/?_cache=false

Brightspot and Dari automatically provide SQL query caching when returning objects, limiting the number of SQL queries on a page where the same content is being requested in two separate queries, thereby consolidating queries.

To turn caching off, add ?_cache=false to the page request. To see the difference, view the change using ?_debug=true&_cache=false. Scroll down to the Dari Profile table, and examine the count for SQL with and without caching.

Context
~~~~~~~

::

    http://yoursite.com/about-us/?_context="module"
    http://yoursite.com/gallery/about-us-gallery/?_context="module"

If context (Documentation for Contextual Rendering) is set on an object, access that context through the URL query parameter ``?_context="contextValue"``.

Contextual Debugger
~~~~~~~~~~~~~~~~~~~

::

    http://yoursite.com/?_debug=true
    http://yoursite.com/about-us/?_debug=true

The Contextual Debugging Tool provides an instant view of webpage metrics. Add ``?_debug=true`` to a page URL to activate the Dari Contextual Debugger. The Contextual Debugger provides a view of the load time, in milliseconds, for each module (JSP) on the page. Color hotspots are added, with relative size, to provide a clear illustration of the slowest loading modules on the page. The larger the circle, and the darker the red, the slower the load time.

Dari Grid
~~~~~~~~~

::

    http://yoursite.com/?_grid=true
    http://yoursite.com/about-us/?_grid=true

The Dari Grid parameter presents the webpage being accessed with an overlay of the grid it is using. Each layout and grid area is labeled, so you can see how the page is constructed visually.

Output Format
~~~~~~~~~~~~~

::

    http://yoursite.com/?_format=json
    http://yoursite.com/?_format=jsonp&_callback=""
    http://yoursite.com/?_format=js
    http://yoursite.com/?_format-json&_result=html

Use the ``?_format=`` parameter to change the output format for the page. Use microdata tags to control this. Documentation available here.

Add ``&_result=html`` to ``?_format=json`` to present the frame response inside JSON.

For JavaScript output, add ``?_format=js``.

Reloader
~~~~~~~~

::

    http://yoursite.com/cms/?_reload=true
    
To automatically trigger the Dari Reloader, which compiles and redeploys any new changes made to the source files, prompt the reload by using ``?_reload=true``. This prompt will begin a reload of source to war.