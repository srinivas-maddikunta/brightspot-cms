Tag Reference Guide
-------------------

Brightspot tags allow you to build templates. Templates provide an easy process for rendering common content types such as links, images, rich text, and objects, and offer functionality for CDN storage of static files and caching.

Images
~~~~~~

The cms:img tag is used to display an image file StorageItem. An object containing a StorageItem or a URL can be passed in to the src attribute. A StorageItem or an object containing a StorageItem can be below the photo property.

.. code-block:: jsp

    <cms:img src="${content.photo}" />

The cms:img tag has a number of attributes that can be set within the tag to specify how the referenced image should display.

**src:** This is the property defined by the StorageItem type. An object or a URL can be passed in and the tag automatically renders a StorageItem attached.

**editor:** Specifies the image editor to use to render the specific image. This is defined in the context.xml:

.. code-block:: xml

    <environment name="dari/defaultImageEditor" override="false" type="java.lang.String" value="_java" />

**size:** Sets the internal crop name of the Standard Image Size to use. This is usually a pre-set image crop size. See Cropping Images for further information.

**width:** This is used to override the width provided by the image size.

**height:** This is used to override the height provided by the image size.

**cropOption:** This is used to override the crop settings provided by the image size attribute. The choice is made editorially in Admin > Settings.

**resizeOption:** This is used to override the resize settings provided by the image size. The choice is made editorially in Admin > Settings.

**hideDimensions:** When set to true, suppresses the "width" and "height" attributes from the final HTML output.

**overlay:** Indicates whether an image object has an overlay object so that it is displayed in the HTML output. The overlay text is added when you select the image crop.

Links
~~~~~

A tag for rendering links, similar to a href. If the object has a defined URL, passing the object itself is sufficient. The object permalink will automatically be used:

.. code-block:: jsp

    <cms:a href="${content}">${content.name}</cms:a>

Rich Text
~~~~~~~~~

The <cms:render> tag is used to render Rich Text (ReferentialText). It automatically renders formatted text and any enhancements (Objects) inserted into the rich text area:

.. code-block:: jsp

    <cms:render value="${content.bodyText}" />

A context attribute can be added to <cms:render> to alter the context of content being rendered within a rich text area. For example, modules added as enhancements may need to use a different rendering file based on the context.

.. code-block:: jsp

    <cms:render context="enhancement" value="${content.bodyText}" />

.. code-block:: java

    @Renderer.Paths ({
        @Renderer.Path("/render/model/gallery-object.jsp"),
        @Renderer.Path(context = "enhancement", value = "/render/module/gallery-module.jsp")
    })
    public class Gallery extends Content {

        @Indexed
        private String name;
        private List<img /> images;

        // Getters and Setters


    }

Rendering objects (Modules/Widgets)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you have a module (Related Content) with it's own JSP to render it @Renderer.Path(), you can pass that content into a <cms:render /> tag, and it will render it:

.. code-block:: java

    // Example Java Class

    public class Article extends Content {

        private String headline;
        private Author author;
        private ReferentialText body;
        private RelatedContent relatedContentModule;

        // Getters Setters
    }
\

.. code-block:: jsp

    <!-- Example JSP -->
    <cms:render value="${content.headline}" />
    <cms:render value="${content.author.name}" />
    <cms:render value="${content.body}" />
    <cms:render value="${content.relatedContentModule}" />

Context can also be added as an attribute in the render tag:

.. code-block:: jsp

    <cms:render context="slideshow" value="${content.image}" />

This will drive the choice of JSP made on the Java class.

Text Markers
^^^^^^^^^^^^

Text Markers create truncation or "Read More" links in the body copy. They can also be used to generate pages for longer bodies of text.

Start by creating a new Referential Text Marker as an option to be inserted into any rich text area, found at Admin > Settings. Click on Marker to see a list of all available text markers.

Truncation

This example allows editors to add a Truncation marker. Start by creating and naming a marker in the CMS.

In the JSP where the truncated text should be used, the cms:render tag can be updated with the endMarker attribute, where the name matches that of the internalName of the text marker in the CMS.

.. code-block:: jsp

    <cms:render endmarker="truncate" value="${content.body}" />

**Page Breaks**

In this example, editors can add text markers to denote where new pages should start in long bodies of text. Start by creating the required text marker for page breaks in Brightspot.

In the JSP rendering the article, get the current page count, based on how many markers were placed:

.. code-block:: jsp

    <c:set var="pageCount" value="${cms:markerCount(content.body, 'pagination-marker')}" /> 

    <div class="container">
    <h1><cms:render value="${content.headline}" /></h1>
    <h5>Written by: <c:out value="${content.author.name}" /></h5>
    <c:choose>
        <c:when test="${pageCount eq 1}">
            <cms:render value="${content.body}" />                    
        </c:when>
        <c:otherwise>
            <cms:render value="${content.body}" beginoffset="${pageNumber &lt; 2 ? '' : pageNumber - 2}" endoffset="${pageNumber == pageCount ? '' : pageNumber - 1}" beginmarker="${pageNumber &lt; 2 ? '' : 'pagination-marker'}" endmarker="${pageNumber == pageCount ? '':'pagination-marker'}" />                    
        </c:otherwise>
    </c:choose>

    <c:if test="${pageCount > 1}">
    <div class="pagination clrfix">
        <ul class="clrfix">
            <li class="prev">
             <c:choose>
                 <c:when test="${pageNumber &lt;= 1}">
                       <a class="prev btn disabled"></a> 
                 </c:when>
                 <c:otherwise>
                    <a class="prev btn" href="${content.permalink}/?page=${pageNumber-1}"></a> 
                 </c:otherwise>
             </c:choose>                   
            </li>
            <li class="status">
                <span class="current">${pageNumber}</span>
                of
                <span class="total">${pageCount}</span>
            </li>
            <li class="next">
             <c:choose>
                 <c:when test="${pageNumber >= pageCount}">
                    <a class="next btn disabled"></a>
                 </c:when>
                 <c:otherwise>
                    <a class="next btn" href="${content.permalink}/?page=${pageNumber+1}"></a>
                 </c:otherwise>
             </c:choose>                   
           </li>
        </ul>
    </div>                 
    </c:if>
    <hr />
    </div>

**cms:context**

Use this tag to set the rendering context across an area.

For example, given the following Gallery with contextual rendering paths:

.. code-block:: java

    @Renderer.LayoutPath("/render/common/page-container.jsp")
    @Renderer.Paths ({
        @Renderer.Path("/render/model/gallery-object.jsp"),
        @Renderer.Path(context = "module", value = "/render/module/gallery-module.jsp")
    })

.. code-block:: java

    public class Gallery extends Content {

        private String name;
        private List<Image> images;

        // Getters and Setters

    }

If the gallery is rendered with the context "module" set, the module value renderer will be chosen:

.. code-block:: jsp

    <cms:context name="module">
    <!-- Gallery as content object -->
        <cms:render value="${content}" />
    </cms:context>

Or in a cms:render tag: <cms:render context="module" value="${content}" />

**cms:cache**

Specify a duration in milliseconds for an item to be cached. In the CMS Template tool, this feature has a UI control element for each section.

.. code-block:: jsp

    <cms:cache name="${}" duration="60000"> </cms:cache>

**cms:layout**

Used to specify the areas in a template or layout. This tag makes use of HTML writers and CSS grids to render the layouts as specified by the areas. The layout object is also referenced in the render tag.

.. code-block:: jsp

    <cms:layout class="layout-global">
        <cms:render value="${header}" area="header"/>
        <cms:render value="${mainContent}" area="main"/>
        <cms:render value="${footer}" area="footer"/>
    </cms:layout>

**cms:inContext**

Use cms:inContext when attempting to check the context. It returns true if the request is in the given context.

Example - Context set and checked <cms:context name="module"> CONTENT HERE </cms:context>

.. code-block:: jsp

    <c:if test="${cms:inContext('module')}">
        <!--Logic for action if in module context -->
    </c:if>

**cms:instanceOf**

Use cms:instanceOf to return true if the given object is an instance of the class represented by the given className.

Example - Show PhotoGallery title only when on the Gallery page, not when it is used as a module:

.. code-block:: jsp

    <c:if test="{cms:instanceOf(mainContent,'com.psddev.PhotoGallery')}">
        <h1><c:out value="${content.name}" /></h1>
    </c:if>

**cms:listLayouts**

When using @Renderer.ListLayouts in your Java class to create a list of potential content types in the CMS for editors to populate, render them in order with cms:listLayouts in the JSP.

See the Page Layouts documentation for more information on using ListLayouts.

.. code-block:: jsp

    <cms:layout class="${cms:listLayouts(content, 'modules')}">
        <cms:render value="${content.modules}" />
    </cms:layout>
    
**cms:js**

Use cms:js to escape the given string so that it is safe to use in JavaScript.

**cms:resource**

The cms:resource function allows files to be automatically uploaded to a default CDN on their first view.

In your context.xml, add: <Environment name="cms/isResourceInStorage" override="false" type="java.lang.Boolean" value="true" />

Point to the local file from within a .jsp file. This can be any kind of file like CSS, JavaScript, or an image file.

.. code-block:: jsp

    <script src="${cms:resource('path/to/file.js')}"></script>
    <img src="${cms:resource('/files/images/image.jpg')}" />

On first view, files that are rendered using the tag will automatically be placed on the default CDN Storage.

On subsequent runs, file changes are automatically detected and new versions are uploaded to the CDN. CSS files are also parsed at runtime, so files contained within CSS, such as background images, are also automatically uploaded.

To add https to the resource, update the context.xml file:

https://s3.amazonaws.com/cdn.yoursite.com

Non-http pages can use https but https pages should only use https.

**cms:frame**

The cms:frame tag allows the designation of an area of a page to be rendered and refreshed independently without reloading the entire page. Use cases include 'load more' functionality, tabbed content, or paginated result sets.

In the example below, page 2 of the results set will be rendered in the <cms:frame> area.

.. code-block:: jsp

    <a target="results" href="${mainContent.permalink}?page=2">See more results</a>

    <cms:frame name="results">

        <!-- Logic to return results -->

    </cms:frame>

Placement of new content in relation to content already loaded in the frame is controlled using the mode attribute. Options include replace, append, and prepend.

.. code-block:: jsp

    <cms:frame mode="append" name="results">
        <!-- New content appears after existing content -->
    </cms:frame>

    <cms:frame mode="replace" name="results">
        <!-- New content replaces existing content -->
    </cms:frame>


    <cms:frame mode="prepend" name="results">
        <!-- New content appears before existing content -->
    </cms:frame>

Control the loading of in-frame content with the lazy attribute. When true, the AJAX request for the in-frame content is sent once the page has loaded:

.. code-block:: jsp

    <cms:frame lazy="true" name="link-module">
        <!--Content loaded in frame when page has loaded -->
    </cms:frame>

**cms:local**

The <cms:local> tag is used to specify the value of a content property within a section of the JSP code. For example, in the code below, the <cms:render> tag is used to render the value in ${content}, but in the <cms:local> tags, the value in ${content} is set to a new value, and that value is rendered only within the cms:local tag.

.. code-block:: jsp

    <cms:render value="${content}" /><!-- outputs oldValue -->
    <cms:local>
        <cms:attr name="content" value="newValue" />
        <cms:render value="${content}" /><!-- outputs newValue -->
    </cms:local>
    <cms:render value="${content}" /><!-- outputs oldValue -->

Within the cms:local tag, a variable can have a new value, but in other parts of the code, the variable has the regular value. The <cms:local> tag is used with the <cms:attr> tag. In the cms:local tag, the cms:attr tag is used to set the new value of the content being previously rendered.