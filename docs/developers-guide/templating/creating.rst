Creating Pages & Templates
--------------------------

Any Brightspot content type can be used to create a page, template, or module by updating the Java class with class level annotations. These annotations point to associated files that define the page structure and render the accessed content type.

Layout Path
~~~~~~~~~~~

.. code-block:: java

    @Renderer.LayoutPath("/render/common/page-container.jsp")

The LayoutPath annotation provides a path to a file, often a common page container, that renders the layout of the page. The file provides the <head> section of pages with stylesheets and a common header and footer for all pages. Brightspot allows any content type to have a URL. Use the URL widget in the right rail of the Content Edit view to define the content's URL. The URL maps to an instance of the object and renders any attached rendering files.

If a content type is used to power an entire page like an Article, Homepage, or Contact Us page, it must have a @Renderer.LayoutPath annotation to define the page layout.

The tag <cms:render value="${mainContent}"/> refers to the content type being rendered and calls the next annotation @Renderer.Path. ${mainContent} will allows return the object associated with the accessed URL.

.. code-block:: jsp

    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
    <%@ taglib prefix="cms" uri="http://psddev.com/cms" %>

    <!DOCTYPE html>
    <html>
    <head>
        <title>${seo.title}</title>
    </head>
        <body>
            <cms:render value="${mainContent}"/>
        </body>
    </html>

Object Path
~~~~~~~~~~~

.. code-block:: java

    @Renderer.Path("/render/model/article-object.jsp")

The Path annotation renders the properties within the content type to which it is attached. It is called when the content is accessed, allowing it to render itself.

If a content type is used to power an entire page like an Article, Homepage, or Contact Us page, it must have a @Renderer.Path annotation to render the content data like the Article headline or Body Text.

Rendering Annotations - Template/Page
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"  %>
    <%@ taglib prefix="cms" uri="http://psddev.com/cms"  %>

    <h1><c:out value="${content.headline}" /></h1>
    <h3><cms:render value="${content.author.name}" /></h3>

    <cms:render value="${content.bodyText}" />