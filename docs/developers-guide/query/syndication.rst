Syndication
-----------

Using Brightspot, you can syndicate and embed individual content types on other sites. The content can retain the original look and feel or inherit new styles.

Add Embed Paths
~~~~~~~~~~~~~~~

You can embed any content with a URL outside of Brightspot. Start by adding an @Renderer.EmbedPath() annotation to the class to be syndicated. The embed.jsp file should contain any JavaScript or CSS needed to power the content, and the cms taglibs for the <cms:render> tag to function.

.. code-block:: java

    @Renderer.EmbedPath("/embed.jsp")
    @Renderer.Path("/generic-module.jsp")
    public class GenericModule extends Content {

        private String title;
        private ReferentialText body;

        // Getters and Setters
    }

Create JSP
~~~~~~~~~~

.. code-block:: jsp

    <link href="file.css" rel="stylesheet" type="text/css"/>
    <script src="file.js" type="text/javascript"></script>

    <cms:render value="${mainContent}"/>

Once you've added a URL, click on the Advanced Tools icon in the content edit view to expose the embed script, which can be added to an external page.

Set the defined Site URL in the Admin -> Settings section called Default Site URL.

.. image:: http://docs.brightspot.s3.amazonaws.com/embed-shot.png

Insert the embed code into any webpage to render the module outside of the Brightspot site.