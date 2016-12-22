Rendering Content
-----------------

Brightspot has out-of-the-box tags that can be used to render content in the front-end template.

Rich Text
~~~~~~~~~

Use the cms:render tag with any ReferentialText object to render formatted text, enhancements or HTML:

.. code-block:: jsp

    <cms:render value="${content.richTextField}" />

Links
~~~~~

Any object with a permalink can be rendered with the <cms:a> tag:

.. code-block:: jsp

    <cms:a href="${content}">Click Here</cms:a>

Images
~~~~~~

Any object with a StorageItem can be placed into the <cms:img> tag:

.. code-block:: jsp

    <cms:img src="${content}" />

Objects
~~~~~~~

Any object with an attached rendering file @Renderer.Path can use the <cms:render> tag to be rendered:

.. code-block:: jsp

    <cms:render value="${content.listModule}" />

See the :doc:`tags` for detailed information about the tags available in Brightspot.