Creating an Article View
~~~~~~~~~~~~~~~~~~~~~~~~

An article's view represents how an article appears on the screen. This typically includes character- and paragraph-level formatting, headers and footers, and other standard features for web pages. 

Brightspot maintains views inside a styleguide.

Step 1: Opening a Blank Styleguide
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In this step, you run and open the blank styleguide.

#. Ensure port 3000 on your local machine is open.
#. Open a new command window and change the current directory to ``brightspot-tutorial/``.
#. Run ``gulp styleguide``. A server starts.
#. In your web browser, open http://localhost:3000/_styleguide/index.html. An empty styleguide appears.

.. image:: article/images/styleguide-blank.png

Step 2: Adding an Article View to the Styleguide
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

By convention, views are in directories parallel to their models. In this step, you create a view for your article model in the appropriate directory.

#. Open a new command window and change the current directory to ``brightspot-tutorial/styleguide/``.
#. Create a directory content/ and a subdirectory article/: ``mkdir -p content/article/``.
#. Change the current directory to the new directory ``content/article/``.
#. Create the file Article.hbs and enter the following text:

.. literalinclude:: /tutorial/snippet/tutorial/article/view/Step1.snippet
   :language: html
   :linenos:
 
5. In the same directory, create a new file Article.json and enter the following text:

.. literalinclude:: /tutorial/snippet/tutorial/article/view/Step2.snippet
   :language: json
   :linenos:
 

6. Refresh the web page running at localhost:3000/_styleguide/index.html. An entry for the article appears. (If you do not see an entry for the article, in the terminal press Ctrl-C to stop the server and then rerun ``gulp styleguide``.)

.. image:: article/images/styleguide-article.png

7. Click **Article**. The style guide displays boilerplate text for the heading and body.

.. image:: article/images/styleguide-default.png


In the previous handlebars listing, lines 3 and 7 specify the boilerplate text appearing in the headline and body; in the previous JSON listing, line 2 specifies the handlebars file your view will use.
 
Step 3: Modifying a View's Boilerplate Text
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The default view you created in the previous step includes standard boilerplate text. You can use helpers to modify the boilerplate text and give authors a better idea of an article's final appearance.

#. Returning to the IDE, update the file Article.hbs with the following:

.. literalinclude:: /tutorial/snippet/tutorial/article/view/Step3.snippet
   :language: html
   :emphasize-lines: 2,4,6,8,10,12
   :linenos:
 

2. Returning to the IDE, update the file Article.json with the following:

.. literalinclude:: /tutorial/snippet/tutorial/article/view/Step4.snippet
   :language: json
   :emphasize-lines: 3,4
   :linenos:

3. Refresh the web page running at localhost:3000/_styleguide/index.html and click **Article**. The new boilerplate text appears.

.. image:: article/images/styleguide-custom.png

In the previous handlebars listing, the ``{{#with}}`` helper specifies which JSON property to use when generating boilerplate text; in the previous JSON listing, lines 3\ |endash|\ 4 specify the value of the boilerplate text for the headline and body. For more information about handlebars helpers and associated JSON properties, see `Brightspot Base <http://docs.brightspot.com.s3-website-us-east-1.amazonaws.com/base/all.html>`_ and  `Handlebars <http://handlebarsjs.com/>`_.

Step 4: Randomizing a View's Boilerplate Text
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Brightspot's JSON files include a helper you can use to randomize the boilerplate text.

#. Returning to the IDE, update the file Article.json with the following:

.. literalinclude:: /tutorial/snippet/tutorial/article/view/Step5.snippet
   :language: json
   :emphasize-lines: 3,4
   :linenos:

2. Refresh localhost:3000/_styleguide/index.html and click **Article**. The boilerplate text is now randomized.

.. image:: article/images/styleguide-randomized.png

In the previous listing, the helpers in lines 3\ |endash|\ 4 generate random text. For more information about the helpers available in a view's JSON file, see `Brightspot Base <http://docs.brightspot.com.s3-website-us-east-1.amazonaws.com/base/all.html>`_.
