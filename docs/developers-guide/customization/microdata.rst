Microdata
---------

By implementing additional tags in your web page's HTML, you can help the search engines and other applications better understand your content and display it in a useful, relevant way. Microdata is a set of tags, introduced with HTML5, that allows you to implement these tags.

Implementing
~~~~~~~~~~~~

Start by finding a content type to contextualize, for example, the Article object:

.. code-block:: java

    public class Article extends Content implements Directory.Item {

        private String headline;
        private Author author;
        private ReferentialText body;

        // Getters and Setters
    }

Go to schema.org to find the matching schema instructions for the content type.

Match the available tags with the fields that exist on the chosen object:

.. code-block:: html

    <div itemscope="" itemtype="http://schema.org/Article">
        <span itemprop="name">Article Name</span> or <span itemprop="headline">Article Headline</span>
        <span itemprop="author">Author Name</span>
        <span itemprop="articleBody">Body Text</span>
    </div>

Add to your JSP
~~~~~~~~~~~~~~~

Add the relevant tags to the JSP:

.. code-block:: jsp

    <div itemscope="" itemtype="http://schema.org/Article">
        <h1><span itemprop="headline"><cms:render value="${content.headline}" /></span></h1>
        <h5>Written by: <span itemprop="author"><c:out value="${content.author.name}" /></span></h5>
        <span itemprop="articleBody"><cms:render value="${content.body}" /></span>
    </div>

View Output
~~~~~~~~~~~

To see the output of the tags and check the content being highlighted for the search engine, go to the page and add ``?_format=json`` to the URL.