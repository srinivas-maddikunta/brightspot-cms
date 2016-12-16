Open Graph
----------

With Open Graph , you can automatically pull content from URLs shared on social media using a variety of commonly used actions and objects.

First, create an object for the site application and extend the Tool class. Then define the Open Graph Meta data fields:

.. code-block:: java

    public class MyApplication extends Tool {

        @Tab("OpenGraph Metadata") //remove
        private String openGraphTitle;

        @Tab("OpenGraph Metadata")
        private String openGraphDescription;

        @Tab("OpenGraph Metadata")
        private String openGraphUrl;

        @Tab("OpenGraph Metadata")
        private String openGraphSiteName;

        @Tab("OpenGraph Metadata")
        private StorageItem openGraphImage;

        // Getters and setters
    }

Once these fields are defined, they will be accessible in the Admin user's Site Settings.

Next, create the class that writes the Open Graph data to the pages on the site. Create a class implementing PageStage.SharedUpdateable:

.. code-block:: java

    public class MyPageStageUpdater implements PageStage.SharedUpdatable {

        //Override the updateStageBefore method in PageStage.SharedUpdatable
        @override
        public void updateStageBefore(final Object object, PageStage stage){

            //Get the MyApplication Object
            MyApplication myApp = Application.Static.getInstance(MyApplication.class);            

            stage.findOrCreateHeadElement("meta",
                    "property", "og:title",
                    "content", myApp.getOpenGraphTitle());

            stage.findOrCreateHeadElement("meta",
                    "property", "og:description",
                    "content", myApp.getMyOpenGraphDescription());

            stage.findOrCreateHeadElement("meta",
                    "property", "og:url",
                    "content", myApp.getOpenGraphUrl());

            stage.findOrCreateHeadElement("meta",
                    "property", "og:site_name",
                    "content", myApp.getOpenGraphSiteName());

            StorageItem openGraphImage = myApp.getOpenGraphImage();
            if (openGraphImage != null) {
                String openGraphImageUrl = openGraphImage.getPublicUrl();
                if (openGraphImageUrl != null) {
                    stage.findOrCreateHeadElement("meta",
                        "property", "og:image",
                        "content", openGraphImageUrl);
                }
            }
        }
    }

The code above creates the MyPageStageUpdater class and uses the findOrCreateHeadElement method to add the Open Graph meta data to each page of the website application.

Include ``<cms:render value="${stage.headNodes}" />`` in the head of the JSP being rendered.

Content Specific Open Graph
~~~~~~~~~~~~~~~~~~~~~~~~~~~

You can also set Open Graph data for specific content on a website. Create a class for the main content and define the values of the Open Graph data:

.. code-block:: java

    public class Article extends Content implements Directory.Item {

        private String openGraphTitle;

        public String getOpenGraphTitle() {
                return title;
        }

        public void setOpenGraphTitle(String title) {
            this.title = title
        }
    }

The Open Graph data will apply to the Article class.

Next, create a class implementing PageStage.SharedUpdateable and call the Article object there to access the Open Graph data. The class will append all Open Graph data to the specific content.

.. code-block:: java

    public class MyPageStageUpdater implements PageStage.SharedUpdateable { 

        @Override
        public void updateStageBefore(final Object object, PageStage stage) {

            if (object instanceof Article) {

                stage.findOrCreateHeadElement("meta",
                    "property", "og:title",
                    "content", ((Article) object).getOpenGraphTitle());
            }
        }
    }

Rendering
~~~~~~~~~

To render the Open Graph data on the front end, include the code below in the main header JSP file of the site.

.. code-block:: jsp

    <cms:render value="${stage.headNodes}" />

Editorial Guide
~~~~~~~~~~~~~~~

Manage Open Graph data for an entire website from the Settings tab under Admin. In the left panel of the Settings page, click the site name under the Applications section. Here, you can enter the Open Graph data for the site application.

