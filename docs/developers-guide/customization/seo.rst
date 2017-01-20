SEO Usage
---------

All content that is assigned a URL appears above a global SEO widget at the bottom of the Content Edit page. This widget provides SEO Title, Description, and Keywords that overwrite the defaults from the content itself.

To appear on the front end, these fields must be chosen in the respective .jsp files.

Click on Content Tools link () to the right of the Publish Widget to see the SEO fields in code.

::

    "cms.seo.title" : "Our New Title",
    "cms.seo.description" : "This description is very different from the default",
    "cms.seo.keywords" : [ "Added", "Are", "Here", "Keywords", "Shown" ],
    "cms.directory.pathsMode" : "MANUAL",
    "cms.directory.paths" : [ "8b48aee0-42d1-11e1-9309-12313d23e8f7/seotest" ],
    "cms.directory.pathTypes" : {
    "8b48aee0-42d1-11e1-9309-12313d23e8f7/seotest" : "PERMALINK"

An example implementation would be to test to see if an SEO Title has been added. If not, the standard object title can be used instead.

.. code-block:: jsp

    <title>Perfect Sense Digital <c:if test="${!empty seo.title}" >: <c:out value="${seo.title}" /></c:if></title>

The SEO Widget in the CMS provides additional robots.txt tools to hide and noindex your content.

.. image:: images/seo-tab.png
