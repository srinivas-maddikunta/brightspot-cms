Error Pages
-----------

You can customize error pages corresponding to the standard Tomcat server error codes. (See `Interface HttpServletResponse <https://tomcat.apache.org/tomcat-9.0-doc/servletapi/javax/servlet/http/HttpServletResponse.html>`_ for a listing of possible Tomcat error codes.) 

#. Open the file :code:`<root>/src/main/webapp/WEB-INF/web.xml`.
#. Under the root :code:`<web-app>` element, define :code:`<error-page>` elements where the :code:`<error-code>` child element corresponds to the tomcat error code and the :code:`<location>` child element corresponds to a subdirectory under :code:`<root>/styleguide/pages/`.
#. Create a new subdirectory under :code:`<root>/styleguide/pages/` that corresponds to the value for :code:`<location>`.
#. In the new subdirectory, create a file :code:`<code>V2.json` that describes the page to display. For more information about describing Brightspot pages as JSON files, see :doc:`../templating/all`.

The following example describes how to customize an error page for error code 404.

#. Open the file :code:`<root>/src/main/webapp/WEB-INF/web.xml`.
#. Under the root :code:`<web-app>` element, define an :code:`<error-page>` entry.

.. code-block:: xml

   <web-app>
      <error-page>
         <error-code>404</error-code>
         <location>/404</location>
      </error-page>
   </web-app>

3. Create a new subdirectory :code:`<root>/styleguide/pages/404/`.
#. In the new subdirectory, create a file :code:`404V2.json` that describes the page to display. 

.. code-block:: json

   {
      "_wrapper": "/styleguide/_WrapperV2.json",
      "_template": "/styleguide/legacy/util/Page.hbs",
      "body": {
         "_template": "/node_modules/brightspot-base/styleguide/core/Concatenated.hbs",
         "items": [
            {
               "_template": "/styleguide/lead/Lead.hbs",
               "headline": "Page Not Found",
               "media": {
                  "_dataUrl": "/styleguide/art-directed-media/ArtDirectedMedia.json"
               }
            }
         ]
      }
   }

