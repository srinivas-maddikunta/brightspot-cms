Page Staging
------------

The PageStage class allows you to insert custom styles for a specific page, and easily update and render the contents of the <head> element.

Start by implementing PageStage.Updatable within a class. The example below shows how to construct the title within a <head> tag.

Usage
~~~~~

::

    public class Article extends Content implements PageStage.Updatable {

        private String title;

        public String getTitle() {
            return title;
        }

        @Override
        public void updateStage(PageStage stage) {
            stage.setTitle(getTitle());
        }

    }

Place the following tag in the JSP file to include the extra headNodes.

::

    <cms:render value="${stage.headNodes}" />

Including Scripts
~~~~~~~~~~~~~~~~~

Add stylesheets or JavaScript through PageStage.Updatable

::

    @Override
    public void updateStage(PageStage stage) {
        stage.addStyleSheet("http://static.yoursitename.com/css/section-global.css");
        stage.addScript("/static/js/jquery-1.9.0.min.js");

    }

These are added into the <head> section of the page using the <cms:render value="${stage.headNodes}" /> tag.

Sharing Logic
~~~~~~~~~~~~~

Rather than applying logic to each class, pages with similiar update logic can share one common class where the updates are contained. This is done using the PageStage.UpdateClass annotation. Start by creating the logic and implementing PageStage.SharedUpdatable:

::

    @Override
    public class DefaultPageStageUpdater implements PageStage.SharedUpdatable {

        @Override
        public void updateStage(PageStage stage) {
        stage.addStyleSheet("http://static.yoursitename.com/css/section-global.css");
        stage.addScript("/static/js/jquery-1.9.0.min.js");   
        }

        @Override
        public void updateStageBefore(Object object, PageStage stage) {
        }

        @Override
        public void updateStageAfter(Object object, PageStage stage) {
            stage.setTitle(stage.getTitle() + " | Site Name");
        }
    }

Next, annotate the class that is to share the logic.

::

    @PageStage.UpdateClass(DefaultPageStageUpdater.class)
    public class Article extends Content {

    }