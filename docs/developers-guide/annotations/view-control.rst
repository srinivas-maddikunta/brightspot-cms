View Control
------------

Content Types can be turned into pages, templates or modules in Brightspot by associating rendering files. These are associated through class level annotations.

Layout Path

.. code-block:: java

    @Renderer.LayoutPath("/render/common/page-container.jsp")

The LayoutPath annotation provides a path to a file that renders the layout of the page. This is often a common page container, providing the <head> section of pages (with stylesheets), as well as a common header and footer for all pages.

If a content type is used to power an entire page (Article / Homepage / Contact Us Page), it must have a @Renderer.LayoutPath annotation to define the page layout.

Example Layout Path File:

.. code-block:: jsp

    <!DOCTYPE html>

    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
    <%@ taglib prefix="cms" uri="http://psddev.com/cms" %>


    <html>
    <head>
        <title>${seo.title}</title>
    </head>
    <body>
        <div class="container">
            <cms:render value="${mainContent}"/>
        </div>
    </body>
    </html>

**Object Path**

.. code-block:: java

    @Renderer.Path("/render/model/article-object.jsp")

The Path annotation renders the properties within the content type it is attached to. It is called when the content is accessed, allowing it to render itself.

If a content type is used to power an entire page (Article / Homepage / Contact Us Page), it must have a @Renderer.Path annotation which is used to render the content data (Article headline / Body Text).

**Rendering Annotations - Template/Page**

.. code-block:: java

    @Renderer.LayoutPath("/render/common/page-container.jsp")
    @Renderer.Path("/render/model/article-object.jsp")
    public class Article extends Content {

        @Indexed
        private String headline;
        private Author author;
        private ReferentialText bodyText;

        // Getters and Setters


    }

Example Article Object Path File:

.. code-block:: jsp

    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
    <%@ taglib prefix="cms" uri="http://psddev.com/cms" %>

    <h1><c:out value="${content.headline}"/></h1>
    <h3><cms:render value="${content.author.name}"/></h3>
    <p>
    <cms:render value="${content.bodyText}"/>
