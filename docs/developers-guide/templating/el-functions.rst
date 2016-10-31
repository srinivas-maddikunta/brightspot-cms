El Functions
------------

cms:inContext
~~~~~~~~~~~~~

Use cms:inContext to check the context. It returns true if the request is in the given context.

Example - Context set and checked

.. code-block:: jsp

    <cms:context name="module"> CONTENT HERE </cms:context>
    <c:if test="${cms:inContext('module')}">
    // Logic for action if in module context
    </c:if>

cms:instanceOf
~~~~~~~~~~~~~~

Use cms:instanceOf to return true if the given object is an instance of the class represented by the given className.

Example - Show PhotoGallery title only when on the Gallery page, not when used as a module:

.. code-block:: jsp

    <c:if test="${cms:instanceOf(mainContent,'com.psddev.PhotoGallery')}">
        <h1><c:out value="${content.name}" /></h1>
    </c:if>
    
cms:listLayouts
~~~~~~~~~~~~~~~

When using @Renderer.ListLayouts in a Java class to create a list of potential content types in Brightspot for editors to populate, render them in order with cms:listLayouts in the JSP.

.. code-block:: jsp

    <cms:layout class="${cms:listLayouts(content, 'modules')}">
        <cms:render value="${content.modules}" />
    </cms:layout>

cms:html
~~~~~~~~

Use cms:html to escape the given string so that it is safe to use in HTML.

cms:js
~~~~~~

Use cms:js to escape the given string so that it is safe to use in JavaScript.

cms:query
~~~~~~~~~

Use cms:query to build a query. The example below finds the widget class, which extends tool, so .first() can be used. Requires Tomcat 7.

.. code-block:: jsp

    <c:set var="content" value="${cms:query('com.psddev.brightspot.utils.Widget').first()}" />
    <cms:render value="${content.widgetName}" />
