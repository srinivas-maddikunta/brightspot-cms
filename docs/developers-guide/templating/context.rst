Contextual Rendering
--------------------

Brightspot provides the ability to render content differently for different display contexts.

Add Renderers
~~~~~~~~~~~~~

If an Article needs to be used as a page template and also a module, it can have two distinct rendering files for each implementation. The default view, which will be called when the @Renderer.LayoutPath asked for <cms:render value="${mainContent}" />, will be the file without a defined context, as in the example below /render/model/article-object.jsp:

.. code-block:: java

    @Renderer.LayoutPath("/render/common/page-container.jsp")
    @Renderer.Paths ({
        @Renderer.Path("/render/model/article-object.jsp"),
        @Renderer.Path(context = "module", value = "/render/module/article-module.jsp")
    })
    public class Article extends Content {

        @Indexed
        private String headline;
        private Author author;
        private ReferentialText bodyText;

        // Getters and Setters

    }

Set Context
~~~~~~~~~~~

Having set the different paths and the context terms on the content type, the matching context term in the file where the content is being rendered must be set with either <cms:context> or through an attribute on <cms:render>:

.. code-block:: jsp

    <cms:context name="module">
        <!-- Content refers to the Article object -->
        <cms:render value="${content}" />
    </cms:context>

    <!-- Set as attribute in cms:render tag -->
    <cms:render context="module" value="${content}" />
    
Testing the Context
~~~~~~~~~~~~~~~~~~~

Test the context using ${cms:inContext('')}:

.. code-block:: jsp

    <c:if test="${cms:inContext('content')}">
        // Render in specific way
    </c:if>

Preview Context
~~~~~~~~~~~~~~~

Content that has various views based on context can be previewed in each view using the Context drop down in the preview tool.

Control view with URL

Requested a specific view by appending the query parameter ?_context="" to a URL for the content type.