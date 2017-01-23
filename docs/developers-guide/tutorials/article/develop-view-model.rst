Creating a View Model
~~~~~~~~~~~~~~~~~~~~~

In the MVVM pattern, a view model contains the logic for extracting data from the model in a form the view can use.

Step 1: Declaring a View Model
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
In this step you declare a view model that implements methods for extracting data required by the article’s view.

#. Returning to IDE, in the same directory as Article.java, create a new file ArticleViewModel.java and enter the following text:

.. literalinclude:: ../../../tutorial/snippet/tutorial/article/view-model/Step2.snippet
   :language: java
   :linenos:


Step 2: Connecting the Article's Model to Its View
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This step implements the methods required to bind the article’s model to the view model created in the previous step. 

#. Returning to the IDE, update the file Article.java with the following:

.. literalinclude:: ../../../tutorial/snippet/tutorial/article/view-model/Step3.snippet
   :language: java
   :linenos:
   :emphasize-lines: 4,6,9

2. Refresh the web page running at localhost:9480, and click **Preview**.

In the previous listing, the ``@ViewBinding`` annotation provides the link between the model and its view. For more information about the @ViewBinding annotation, see `Annotation Type Viewbinding <https://artifactory.psdops.com/psddev-releases/com/psddev/cms/3.2.6504-ad4fbd/cms-3.2.6504-ad4fbd-javadoc.jar!/com/psddev/cms/view/ViewBinding.html>`_. 

