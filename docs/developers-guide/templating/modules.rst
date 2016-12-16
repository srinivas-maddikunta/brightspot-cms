Creating Modules
----------------

Any content type can be used to create a module. Modules typically have a single rendering annotation, @Renderer.Path, placed on the class, so that when it is placed on a page it renders itself. It does not need the @Renderer.LayoutPath annotation, as no page structure is included when requesting the module.

Render Modules
~~~~~~~~~~~~~~

.. code-block:: java

    @Renderer.Path("/render/modules/text-module.jsp")
    public class TextModule extends Content {

        private String name;
        private Author author;
        private ReferentialText bodyText;

        // Getters and Setters


    }

When called from <cms:render>, the text-module.jsp will be accessed to render the module.

Preview Modules
~~~~~~~~~~~~~~~

You can preview modules as they are placed on pages or you can preview them on their own. To preview a module by itself, a specific rendering container that provides the styles, sheets, and scripts needed for the module is added to the class. A preview width is also provided as an option.

.. code-block:: java

    @Renderer.EmbedPath("/render/common/module-preview.jsp")
    @Renderer.EmbedPreviewWidth(300)
    @Renderer.Path("/render/modules/text-module.jsp")
    public class TextModule extends Content {

        private String name;
        private Author author;
        private ReferentialText bodyText;

        // Getters and Setters


    }

The EmbedPath file should include all required styles and scripts for the module and a <cms:render value="${mainContent}" /> tag to render the module content:

.. code-block:: jsp

    <link href="/static/css/main.css" rel="stylesheet" />
    <link href="http://fonts.googleapis.com/css?family=Lato:300,400,700,300italic,400italic" rel="stylesheet" type="text/css" />
    <link href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet" />
    <script src="/static/js/jquery.min.js"></script>

    <cms:render value="${mainContent}" />