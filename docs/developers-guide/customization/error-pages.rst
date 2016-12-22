Error Pages
-----------

Error Page Element
~~~~~~~~~~~~~~~~~~

Add error-page elements to the web.xml and specify the URL on which they are accessed. In the example below, the 404 error code maps to /404. This could also be /no-page-found:

.. code-block:: xml

    <error-page>
        <error-code>404</error-code>
        <location>/404</location>
    </error-page>

    <error-page>
        <error-code>505</error-code>
        <location>/505</location>
    </error-page>

Error Page Object
~~~~~~~~~~~~~~~~~

Create an object that renders a visual error page. Add the URL that matches the mapping in the web.xml.

.. code-block:: java

    @Renderer.Path("/render/common/error.jsp")
    @Renderer.LayoutPath("/render/common/page-container.jsp")
    public class ErrorPage extends Content {

        private String errorMessage;

        // Getters and Setters
    }

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/a10d006/2147483647/resize/700x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2Faf%2F3f%2Ffd7beb6a42e09c7b8ee941ba64ab%2Fscreen-shot-2014-11-14-at-24703-pmpng.24.35.png