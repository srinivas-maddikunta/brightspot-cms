Creating an Article View
~~~~~~~~~~~~~~~~~~~~~~~~

An article's view represents how an article appears on the screen. This typically includes character- and paragraph-level formatting, headers and footers, and other standard features for web pages. 

Brightspot implements views in two components: a template in a Handlebars file, and data in a JSON file. When rendering a view, Brightspot hydrates the display with data in the JSON file. 

Step 1: Opening a Blank Styleguide
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Brightspot maintains views inside a styleguide. In this step, you run and open the blank styleguide.

#. Ensure port 3000 on your local machine is open.
#. Open a new command prompt (Windows) or Terminal (Mac/\*nix), and change the current directory to :code:`brightspot-tutorial/`.
#. Run :code:`gulp styleguide`. A server starts.
#. In your web browser, open http://localhost:3000/_styleguide/index.html. An empty styleguide appears.

.. image:: article/images/styleguide-blank.png

Step 2: Adding an Article View to the Styleguide
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

By convention, a view's template and data are in directories parallel to its model. In this step, you create a template and data for your view in the appropriate directory.

#. Returning to the IDE, create the file :code:`Article.hbs` in the directory :code:`brightspot-tutorial/styleguide/content/article/`, and enter the following text:

.. literalinclude:: /tutorial/docs/article/Step5.snippet
   :language: html
   :linenos:
 
2. In the same directory, create a new file :code:`Article.json` and enter the following text:

.. literalinclude:: /tutorial/docs/article/Step6.snippet
   :language: json
   :linenos:
 

3. Refresh the web page running at :code:`localhost:3000/_styleguide/index.html`. An entry for the article appears. (If you do not see an entry for the article, in the terminal press Ctrl-C to stop the server and then rerun :code:`gulp styleguide`.)

.. image:: article/images/styleguide-article.png

4. Click **Article**. The style guide displays static text for the heading and body.

.. image:: article/images/styleguide-default.png


In the previous Handlebars snippet, lines 3 and 7 specify the static text appearing in the headline and body; in the previous JSON snippet, line 2 specifies the Handlebars file your view will use.
 
Step 3: Modifying a View's Static Text
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The default view you created in the previous step includes standard static text. You can use helpers to modify the static text and give authors a better idea of an article's final appearance.

#. Returning to the IDE, update the file :code:`Article.hbs` with the following:

.. literalinclude:: /tutorial/docs/article/Step7.snippet
   :language: html
   :emphasize-lines: 2,4,6,8,10,12
   :linenos:
 

2. Returning to the IDE, update the file :code:`Article.json` with the following:

.. literalinclude:: /tutorial/docs/article/Step8.snippet
   :language: json
   :emphasize-lines: 3,4
   :linenos:

3. Refresh the web page running at :code:`localhost:3000/_styleguide/index.html` and click **Article**. The new static text appears.

.. image:: article/images/styleguide-custom.png

In the previous Handlebars snippet, the :code:`{{#with}}` helper specifies which JSON property to use when generating static text; in the previous JSON snippet, lines 3\ |endash|\ 4 specify the value of the static text for the headline and body. For more information about Handlebars helpers and associated JSON properties, see the `Brightspot Styleguide repository <https://github.com/perfectsense/brightspot-styleguide>`_ and  `Handlebars <http://handlebarsjs.com/>`_.

Step 4: Randomizing a View's Static Text
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Brightspot's JSON files include a helper you can use to randomize the static text.

#. Returning to the IDE, update the file :code:`Article.json` with the following:

.. literalinclude:: /tutorial/docs/article/Step9.snippet
   :language: json
   :emphasize-lines: 3,4
   :linenos:

2. Refresh :code:`localhost:3000/_styleguide/index.html` and click **Article**. The static text is now randomized.

.. image:: article/images/styleguide-randomized.png

In the previous snippet, the helpers in lines 3\ |endash|\ 4 generate random text. For more information about the helpers available in a view's JSON file, see `Brightspot Styleguide repository <https://github.com/perfectsense/brightspot-styleguide>`_.
