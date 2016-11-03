###########
Hello World
###########

The Hello World guide will take you through a simple tutorial with a new Brightspot project, including using Brightspot to create a webview and generating and using the Dari API.

Additional documentation is available for both `Brightspot <https://artifactory.psdops.com/psddev-releases/com/psddev/cms/3.2.5745-1cb7d2/cms-3.2.5745-1cb7d2-javadoc.jar!/index.html>`_ and `Dari <https://artifactory.psdops.com/psddev-releases/com/psddev/dari/3.2.2188-2d7dae/dari-3.2.2188-2d7dae-javadoc.jar!/index.html>`_.

*******************
Create Content Type
*******************

With Brightspot running locally, open the Maven project with an IDE or text editor. Go to src/main/java/package/name/ to create a new Content Type, Article, alongside the package-info file. Save as Article.java.

.. code-block:: java

    package project.package.name;

    import com.psddev.dari.db.*;
    import com.psddev.dari.util.*;
    import com.psddev.cms.db.*;
    import java.util.*;

    public class Article extends Content {


    }


**********************
Content User Interface
**********************

Open the Brightspot application in a browser at `http://localhost:8080/cms <http://localhost:8080/cms>`_. If you're logging in for the first time, a login prompt will appear.

Provide Brightspot with your email or username and password and you'll be registered as an administrator.

Once logged in, open the global Search tool by clicking into the search box on the Dashboard view. The new Article Content Type will be available in the Types drop-down.

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/136bf96/2147483647/resize/700x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2F97%2F2a%2Fdd867c814075b243e5086a50b334%2Fscreen-shot-2014-12-03-at-122227-pmpng.22.27%20PM.png

Select the Article Content Type and click New to open a blank new article in the content edit view.

***************
Add Field Types
***************

You can build a user interface with a mix of standard Java types and out-of-the-box Brightspot types.

Update the Article class with some basic field types.

.. code-block:: java

    public class Article extends Content {

        private String headline;
        private ReferentialText bodyText;
        private List<String> keywords;

        // Getters and Setters

    }

Save the class and refresh the browser. The new fields will be added to the Article Content Type. Add your content and click Publish.

.. note:: 

    A prompt to install a Reloader application may appear in the top right of the browser. This is the Dari on-the-fly Java code reloader installed in the webapps directory of Tomcat. It reloads changes on-the-fly for Java code, eliminating the need to redeploy the Java application for every change. If refreshing does not prompt the reloader, add ?_reload=true to the end of the URL or stop the project, rebuild, and redeploy.

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/0e04f69/2147483647/resize/700x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2F89%2F65%2F95c6291b47eca92b2d0e3e99a4df%2Fscreen-shot-2014-12-03-at-122312-pmpng.23.12%20PM.png

Add annotations to the fields to control validation, add user interface elements and notes, and index content for querying.

.. code-block:: java

    public class Article extends Content {

        @Required
        @Indexed
        private String headline;
        @ToolUi.Note("Body of text for the article")
        private ReferentialText bodyText;
        private List<String> keywords;

        // Getters and Setters


    }

**************
Render Content
**************

Any Object extending the Brightspot parent class Content can be used to power a page, template, or module.

Update the Article class with a Layout renderer, which provides the page structure, and a Path renderer, which renders the content on the page (The HelloWorld fields).

.. code-block:: java

    @Renderer.LayoutPath("/render/page-container.jsp")
    @Renderer.Path("/render/article.jsp")
    public class Article extends Content {

        @Required
        @Indexed
        private String headline;
        @ToolUi.Note("Body of text for the article")
        private ReferentialText bodyText;
        private List<String> keywords;

        // Getters and Setters
    }

**@Renderer.LayoutPath**

Import the basic JSTL taglibs and use the <cms:render> tag to request the mainContent (Article).

.. code-block:: java

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

**@Renderer.Path**

When <cms:render value="${mainContent}"> is called, the @Renderer.Path is accessed. Access field data by using the property name <cms:render value="${content.fieldName}">.

.. code-block:: java

    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
    <%@ taglib prefix="cms" uri="http://psddev.com/cms" %>

    <h1><c:out value="${content.headline}"></c:out></h1>

    <cms:render value="${content.bodyText}"/>

Return to Brightspot and add a URL to the article. Open the Preview or click the URL to see the rendered content.

*********
Query API
*********

To create a module that shows a list of all Articles in the database, use the Dari Query API instead of or in addition to a web view. Brightspot provides a database-abstraction API through Dari so you can retrieve content. Queries are represented by instances of the Dari Query class, which should look familiar if you've used SQL before.

.. code-block:: java

    Query.from(Article.class).selectAll()

You can use the Dari code tool, accessible at http://locahost:8080/_debug/code, to test any queries:

.. code-block:: java

    public class Code {
        public static Object main() throws Throwable {
            return Query.from(Article.class).selectAll();
        }
    }

The results are returned in JSON on the right side of the Code tool. To add this as a method accessible in the front-end module markup, add a getArticles() method to a new Module:

.. code-block:: java

    @Renderer.Path(/article-list-module.jsp)
    public class ArticleListModule {

        private String name;

        // getter and setter

        public List<Article> getArticles(){
            return Query.from(Article.class).selectAll();
    }

In the module rendering file (article-list-module.jsp):

.. code-block:: java

    <h1>${content.name}</h1>
    <c:forEach items="${content.articles}" var="item">
        <cms:a href="${item}">${item.headline}</cms:a>
    </c:forEach>

Typically, modules are added to pages as content types in the main content type (Article). Where multiple modules can be chosen, You can create an interface with all applicable modules implementing the interface. For example, Right Rail modules:

.. code-block:: java

    public interface RightRail extends Recordable {

    }

.. code-block:: java

    @Renderer.LayoutPath("/render/page-container.jsp")
    @Renderer.Path("/render/article.jsp")
    public class Article extends Content {

        @Required
        @Indexed
        private String headline;
        @ToolUi.Note("Body of text for the article")
        private ReferentialText bodyText;
        private List<String> keywords;

        @ToolUi.Tab("Page Modules")
        private List<RightRail> rightRailModules;

        // Getters and Setters


    }
    
You can use the <cms:render> tag to render a list of content types, such as modules, and it will render each using their assigned Renderer.Path jsp.

**********
Custom API
**********

To access all articles externally, you can generate an API for use outside of Brightspot:

.. code-block:: java

    @RoutingFilter.Path(value = "/api-feed")
    public class FeedServlet extends HttpServlet {

        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

            List<Map<String, Object>> articleFeed = new ArrayList<Map<String, Object>>();

                List<Article> articles = Query.from(Article.class).where(Content.PUBLISH_DATE_FIELD + " != missing").sortDescending(Content.PUBLISH_DATE_FIELD).select(0, 100).getItems();
                for (Article article : articles) {
                    Map<String, Object> articleJson = new HashMap<String, Object>();
                    articleJson.put("title", article.getHeadline());
                    articleJson.put("body", article.getBody());
                    articleJson.put("link", article.getPermalink());

                    articleFeed.add(articleJson);
                }

                response.setContentType("application/json");
                response.getWriter().write(ObjectUtils.toJson(articleFeed));
        }
    }
    
Access http://www.yoursite.com/api-feed to view a JSON result of the constructed query.