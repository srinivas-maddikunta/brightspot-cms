Creating a View Model
~~~~~~~~~~~~~~~~~~~~~

In the MVVM pattern, a view model contains the logic for extracting data from the model in a form the view can use.

Step 1: Declare a View Model
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
In this step you declare a view model that implements methods for extracting data required by the article’s view.

#. Returning to IDE, in same directory as Article.java, create a new file ArticleViewModel.java and enter the following text:

.. code-block:: java

   package content.article;

   import com.psddev.cms.view.ViewModel;
   import styleguide.content.article.ArticleView;

   public class ArticleViewModel extends ViewModel<Article> implements ArticleView {

   }

Step 2: Connect the Article's Model to Its View
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This step implements the methods required to bind the article’s model to the view model created in the previous step. 

#. In the text editor, return to the file ArticleViewModel.java and enter the following text:

.. code-block:: java

   package content.article;

   import com.psddev.cms.view.ViewModel;
   import styleguide.content.article.ArticleView;

   public class ArticleViewModel extends ViewModel<Article> implements ArticleView {

      @Override
      public String getBody() {
         return model.getBody();
      }

      @Override
      public String getHeadline() {
         return model.getHeadline();
      }
   }

2. Refresh the web page running at localhost:9480, and click **Preview**.

