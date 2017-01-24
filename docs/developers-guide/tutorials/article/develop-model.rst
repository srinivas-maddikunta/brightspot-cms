Developing an Article
---------------------

In this tutorial, you'll deploy a small Brightspot news site for exposing extra-terrestrial affairs. The site will include a model for articles, a view for articles, and a view model that binds the model to the view. The following diagram summarizes the progression of this tutorial.

.. image:: article/images/progression.png

Time Required
~~~~~~~~~~~~~

Estimated time to complete this tutorial is 40 minutes.

What You Should Know
~~~~~~~~~~~~~~~~~~~~

To successfully complete this tutorial you should know or understand the following:

- Java
- git
- Java IDE or text editor
- In this tutorial you will be creating view by modifying handlebars and JSON files. Creating complex views requires advanced knowledge of these file types.


Download and Run the Brightspot Tutorial Project
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

#. Ensure port 9480 on your local machine is open, and ensure you have an environment variable JAVA_HOME pointing to *<jdk_root>*, such as /usr/local/java/jdk/jdk1.8.0_121/.
#. Open a terminal, and change to a directory where you want to install the Brightspot project.
#. At the command line type ``git clone -b init https://github.com/perfectsense/brightspot-tutorial``.
#. If you are using an IDE, import the Brightspot project starting from the directory ``brightspot-tutorial/``.
#. Change the current directory to ``brightspot-tutorial/``.
#. Type ``./run.sh`` (Linux, OS X) or ``run.cmd`` (Windows).
#. The installation script requires about three minutes to download components, compile, and display at \http://localhost:9480/cms/logIn.jsp. A form for entering a username and password appears.

.. image:: article/images/login.png

8. Enter your username and password. The dashboard appears. 

.. image:: article/images/dashboard.png


Project Structure Conventions
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Brightspot projects are comprised of two components:

Back-end code\ |emdash|\ These are the model and view-model files coded in Java. These files are stored in the directory src/main/java/.
Front-end code\ |emdash|\  These are the view files coded in JSON and handlebars. These files are stored in the directory styleguide/.

In addition, Brightspot automatically generates files for each content type in parallel directories under target/generated-sources/.

There are MVVM files for each type of content. A best practice is to create subdirectories for each type of content under the content/folder. Referring to the following diagram, source and generated files for an article are under the various content/article/ subdirectories.


.. code-block:: none
<root>
   ├── src
   │   └── main
   │       └── java
   │           ├── content
   │           │   └── article
   │           │       ├── Article.java
   │           │       └── ArticleViewModel.java
   │           └── internal
   │      
   ├── styleguide
   │   └── content
   │       └── article
   │           ├── Article.hbs
   │           └── Article.json
   │      
   └── target
       └── generated-sources
           ├── annotations
           └── styleguide
               └── styleguide
                   └── content
                       └── article
                           └── ArticleView.java

Creating an Article Model
~~~~~~~~~~~~~~~~~~~~~~~~~

An article's model represents the business logic and data validation associated with an article. The business logic typically specifies which components comprise an article, and data validation describes how those components are populated. 


Step 1: Declaring the Article Class
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In this step you declare a Java class for articles.

#. Open a new terminal, and change the current directory to ``brightspot-tutorial/src/main/java/``.
#. Create a directory content/ and a subdirectory article/: ``mkdir -p content/article/``.
#. Change the current directory to ``content/article/``.
#. In an IDE or text editor, create the file Article.java and enter the following text:


.. literalinclude:: /tutorial/snippet/tutorial/article/model/Step1.snippet
   :language: java
   :linenos:

5. Refresh the web page running at localhost:9480.
#. In the dashboard's banner, click in the search field, and in the panel that opens click **New** to create a new article. The article model appears.

.. image:: article/images/new-article.svg


The model appearing in the browser has no data-entry fields. You'll add those in the next step.

Step 2: Adding Fields to the Article Model
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In the MVVM pattern the model includes business logic. In this step, you implement business logic stating that an article has two components: a headline and a body.

#. Returning to the IDE, update the file Article.java with the following:

.. literalinclude:: /tutorial/snippet/tutorial/article/model/Step2.snippet
   :language: java
   :linenos:
   :emphasize-lines: 7-24

2. Refresh the web page running at localhost:9480. A note appears in the right-hand side of the banner to install the reloader. 


.. image:: article/images/reloader.png


3. Click the link to install the reloader. The model reloads and now has two fields.

.. image:: article/images/new-fields.png

In the previous listing, the properties ``headline`` and ``body`` in lines 7\ |endash|\ 8 indicate your article has headline and body fields, and the setter and getter methods in lines 10\ |endash|\ 24 save and display the current values of those fields in the model.


Step 3: Making a Field Required
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In addition to business logic, the MVVM model also specifies data validation. In this step, you include a validation rule that each article must have a headline.

#. Returning to the IDE, update the file Article.java with the following:

.. literalinclude:: /tutorial/snippet/tutorial/article/model/Step3.snippet
   :language: java
   :linenos:
   :emphasize-lines: 4,8


2. Refresh the web page running at localhost:9489. The headline field now includes a hint that the field is required.

.. image:: article/images/required.png


In the previous listing, the annotation ``@Recordable.Required`` in line 8 specifies that the headline field is required. Brightspot has many @Recordable annotations for implementing data validation. For details, see `Interface Recordable <http://www.dariframework.org/javadocs/com/psddev/dari/db/Recordable.html>`_.

Step 4: Adding a Rich-Text Editor
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

A rich-text editor (RTE) provides controls for formatting at the character and paragraph level. In this step, you recast the body field to include an RTE.

#. Returning to the IDE, update the file Article.java with the following:
 
.. literalinclude:: /tutorial/snippet/tutorial/article/model/Step4.snippet
   :language: java
   :linenos:
   :emphasize-lines: 4,12


2. Refresh the web page running at localhost:9480. The body field is now cast as an RTE.

.. image:: article/images/rte-editor.png


In the previous listing, the annotation ``@ToolUi.RichText`` in line 12 specifies that the body field is cast as an RTE. Brightspot has many @ToolUi annotations for casting data-entry fields. For details, see `Class ToolUi <https://artifactory.psdops.com/psddev-releases/com/psddev/cms/3.2.6504-ad4fbd/cms-3.2.6504-ad4fbd-javadoc.jar!/com/psddev/cms/db/ToolUi.html>`_.


Step 5: Composing an Article
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Now that you have a model, it's time for you to write an article announcing the arrival of aliens on our planet. As you make changes in fields, Brightspot highlights the field name.

#. In the Headline field, enter headline for the blog article.
#. In the Body field, enter the article's body.
#. Click **Save Draft**.

.. image:: article/images/blog-article.png

