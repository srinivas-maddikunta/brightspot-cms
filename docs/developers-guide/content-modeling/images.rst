Images
------

Image Setup
~~~~~~~~~~~

Brightspot includes a wide variety of image editing capabilities, including cropping, brightness, sharpness and color adjustment, image rotation, blurring, and text overlays.

To begin using and editing images in Brightspot, define a storage mechanism.

Choosing Storage
~~~~~~~~~~~~~~~~

Brightspot supports CDN storage of files. Add the configuration information to the context.xml file, where the defaultStorage is provided:

::

    <environment name="dari/defaultStorage" type="java.lang.String" value="STORAGE_VALUE" override="false">

Local Storage
^^^^^^^^^^^^^

::

    <environment name="dari/storage/STORAGE_VALUE/class" override="false" type="java.lang.String" value="com.psddev.dari.util.LocalStorageItem">
    <environment name="dari/storage/STORAGE_VALUE/rootPath" override="false" type="java.lang.String" value="/path/to/tomcat/webapps/storage-dir">
    <environment name="dari/storage/STORAGE_VALUE/baseUrl" override="false" type="java.lang.String" value="http://localhost:8080/storage-dir">

The storage-dir must be created in the webapps directory of Tomcat.

CDN
^^^

::

    <environment name="dari/storage/STORAGE_VALUE/class" override="false" type="java.lang.String" value="com.psddev.dari.util.AmazonStorageItem">
    <environment name="dari/storage/STORAGE_VALUE/baseUrl" override="false" type="java.lang.String" value="BASE_URL">
    <environment name="dari/storage/STORAGE_VALUE/access" override="false" type="java.lang.String" value="ACCESS_ID">
    <environment name="dari/storage/STORAGE_VALUE/secret" override="false" type="java.lang.String" value="SECRET">
    <environment name="dari/storage/STORAGE_VALUE/bucket" override="false" type="java.lang.String" value="BUCKET_NAME">

If you're using Amazon to upload, add the following dependency to the pom.xml file.

::

    <dependency>
        <groupId>net.java.dev.jets3t</groupId>
        <artifactId>jets3t</artifactId>
        <version>0.8.0</version>
    </dependency>


Choosing an Image Editor
~~~~~~~~~~~~~~~~~~~~~~~~

By default, Brightspot offers a built-in Java Image Editor. Configure more options in the context.xml:

::

    <environment name="dari/defaultImageEditor" override="false" type="java.lang.String" value="_java">
    <environment name="dari/imageEditor/_java/class" override="false" type="java.lang.String" value="com.psddev.dari.util.JavaImageEditor" />
    <environment name="dari/imageEditor/_java/quality" override="false" type="java.lang.String" value="ULTRA_QUALITY" />  <!--— options ULTRA_QUALITY, QUALITY, BALANCED, SPEED, AUTOMATIC   : defaults to automatic —-->
    <environment name="dari/imageEditor/_java/baseUrl" override="false" type="java.lang.String" value="http://image-cache.com/_image/" /> <!--—typically used for image pull through cache such as Amazon CloudFront —-->
    <environment name="dari/imageEditor/_java/errorImage" override="false" type="java.lang.String" value="http://path-to-image.jpg" /> <!--— Image to use when an error occurs —-->

DIMS Image Editor
^^^^^^^^^^^^^^^^^

Brightspot can use `DIMS <https://github.com/beetlebugorg/mod_dims>`_, an open source image editor built on top of Image Magic, to manipulate images. Configure it in context.xml.

::

    <environment name="dari/defaultImageEditor" override="false" type="java.lang.String" value="dims">
    <environment name="dari/imageEditor/dims/class" override="false" type="java.lang.String" value="com.psddev.dari.util.DimsImageEditor">
    <environment name="dari/imageEditor/dims/baseUrl" override="false" type="java.lang.String" value="http://example.com/dims4/APP_ID">
    <environment name="dari/imageEditor/dims/sharedSecret" override="false" type="java.lang.String" value="SECRET">
    <environment name="dari/imageEditor/dims/quality" override="false" type="java.lang.Integer" value="90">

An Image object with StorageItem and renderer must be created to begin using Images. See the Image Object Model section below  for more information.

Image Object Model
~~~~~~~~~~~~~~~~~~

To begin using and editing images in Brightspot, the Image object needs to be created by creating an Image.java class with a StorageItem as the file to be uploaded to the default storage mechanism:

.. code-block:: java

    public class Image extends Content {

        private String name;
        private StorageItem file;
        private String altText;

        // Getters and Setters
    }

Once the Image object is in place, you can begin to work with image content in Brightspot. Several functionalities can be added to image content by adding annotations.

Rendering
^^^^^^^^^

To make an image available to the front end, a renderer must be created and attached to the image. Add the following @Renderer.Path annotation and create a file to render.

.. code-block:: java

    @Renderer.Path("/image.jsp")
    public class Image extends Content {

        private String name;
        private StorageItem file;
        private String altText;

        // Getters and Setters 
    }

The rendering file can use the <cms:img> tag, which automatically applies the storage item in the object as the file to render:

.. code-block:: jsp

    <cms:img src="${content}" alt="${content.altText}" />


Preview
^^^^^^^

To allow an image to be previewed in Brightspot but not, as by default, in the text field, add the following annotation to the Image class: It tells

.. code-block:: java

    @PreviewField("file")
    public class Image extends Content { 

        private String name;
        private StorageItem file;
        private String altText;

        //Getters and Setters
    }

The system will use the StorageItem field as a preview.

Adding to Rich Text
^^^^^^^^^^^^^^^^^^^

You can reference Images in the Rich Text Editor and add them as Enhancements to your content. To enable an image for reference, add the following annotation:

.. code-block:: java

    @ToolUi.Referenceable
    public class Image extends Content { 

        private String name;
        private StorageItem file;
        private String altText;

        //Getters and Setters
    }


Bulk Upload
^^^^^^^^^^^

Brightspot's bulk upload feature, located on the Dashboard, allows you to upload multiple files simultaneously. To upload images and automatically populate any required fields, add the following as a beforeSave to automatically use the original file name as the image name.

.. code-block:: java

    @Override
    public void beforeSave() {
        if (StringUtils.isBlank(name)) {
            if (file != null) {
                Map<string, object=""> metadata = file.getMetadata();
                if (!ObjectUtils.isBlank(metadata)) {
                    String fileName = (String) metadata.get("originalFilename");
                    if (!StringUtils.isEmpty(fileName)) {
                        name = fileName;
                    }
                }
            }
        }
    }


.. _cropping-images:

Cropping Images
~~~~~~~~~~~~~~~

In Brightspot, you can create a collection of default image crops called Standard Image Sizes. Create a new crop in Admin & Settings. Click "New Standard Image Size" on the left side. Give the new crop a name and an internal name, then set the height and width of the crop.

When you've defined a crop, it can be used to control the size of a rendered image.

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/1513fc9/2147483647/resize/700x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2F11%2F12%2F827680de4690a51a5bb1a13442ef%2Fscreen-shot-2014-11-25-at-13906-pmpng.31.39.png

Crop Options
^^^^^^^^^^^^

**Blank:** Uses the Image Sizes crop or allows you to override with a manual crop via the Image Editor.

**None:** No crops allowed, resize only.

**Automatic:** Ignores crops from Image Editor.

Resize Options
^^^^^^^^^^^^^^

**Ignore Asp Ratio:** Will not maintain the aspect ratio of the image. Image may be distorted.

**Only Shrink Larger:** Retains aspect ratio and shrinks the image so that both dimensions are less than or equal to the specified Image Size dimensions.

**Only Enlarge Smaller:** Enlarges the image so that one dimenion is at least equal to the specified height and width.

**Fill Area:** Will make the image fill the dimensions. May result in cropping of the image

The <cms:img> tag has an attribute called size that accepts the internal name of a crop defined in Brightspot. With an article-crop created in Brightspot, it can be set as the crop size in the article-object.jsp file.

Java Class with Image object:

.. code-block:: java

    public class Article extends Content {

        private String headline;
        private Image image;
        private ReferentialText bodyText;

        // Getters and Setters


    }

JSP file rendering image with crop:

.. code-block:: jsp

    <cms:render value="${content.headline}" />
    <cms:img src="${content.image}" size="article-crop" />

If the image object has a JSP rendering it, a crop size can be set in any JSP.

.. code-block:: jsp

    <cms:img src="${content}" size="${imageSize}" />

For example, if an image is added to a Rich Text area and a crop should be passed on the request:

.. code-block:: jsp

    <c:set var="imageSize" value="blog-crop" scope="request" />
    <cms:render value="${content.bodyText}"/>


Image Galleries
~~~~~~~~~~~~~~~

Once you've created an Image object that provides the ability to upload and render images, you can create a Gallery.

The recommended best practice is to create a static class in a Gallery, often called a Slide or ImageContainer, which can have in-context override fields, like a caption.


Gallery Content Type
^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

    @Renderer.Path("/gallery.jsp")
    @Renderer.LayoutPath("/page-container.jsp")
    public class Gallery extends Content { 

        private String name;
        private List<Slide> slides;

        // Getters and Setters

        @PreviewField"image/file")
        public static class Slide extends Content {

            private String name;
            private Image image;
            private String caption;

            //Getters and Setters
        }

    }

Any List of objects that has a StorageItem defined as PreviewField will automatically convert to a grid preview as a list of thumbnails.

Gallery Rendering File
^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: jsp

    <h1>${content.name}</h1>
    <c:foreach items="${content.slides}" var="slide">
        <cms:img src="${slide}" />
        <c:out value="${slide.caption}" />
    </c:foreach>
    </slide>

Image Tag
~~~~~~~~~

Use the cms:img tag to display an image file StorageItem. An object containing a StorageItem or a URL can be passed in to the src attribute. A StorageItem or an object containing a StorageItem can be below the photo property.

.. code-block:: jsp

    <cms:img src="${content.photo}" />

You can set a number of attributes within the tag to specify how the referenced image should display. Frequently used attributes include:

**src:** The property defined by the StorageItem type. An object or a URL can be passed in, and the tag automatically renders a StorageItem attached.

**editor:** Specifies the image editor to render the specific image as defined in the context.xml:

::

    <environment name="dari/defaultImageEditor" override="false" type="java.lang.String" value="_java" />

**size:** Sets the internal crop name of the Standard Image Size to use, typically a pre-set image crop size. See :ref:`cropping-images` for more information.


**width:** Overrides the width provided by the image size.

**height:** Overrides the height provided by the image size.

**cropOption:** Override the crop settings provided by the image size attribute. The choice is made editorially in the Admin & Settings section.

**resizeOption:** Override the resize settings provided by the image size. The choice is made editorially in the Admin & Settings section.

**hideDimensions:** When set to true, suppresses the "width" and "height" attributes from the final HTML output.

**overlay:** Indicates whether an image object has an overlay object so that it is displayed in the HTML output. The overlay text is added when you select the image crop in Brightspot.