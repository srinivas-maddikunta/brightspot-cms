Modifications
-------------

When a property that is not common among a group of objects needs to be added after the objects have been created, you can use the Modification class in `Dari <http://www.dariframework.org/javadocs/com/psddev/dari/db/Modification.html>`_. It provides multiple inheritance to object types from a singular class. There are two types of modifications: those applied to objects created in project application code, and those applied to Brightspot object types that are not part of a project.

Application Object Modifications
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

One example of implementing an application object modification is a global property, promoTitle or promoImage, that needs to be added to a group of objects like Blog, Article, Image, or Slideshow. They do not inherit from one global class that can be changed, so there is no quick means to apply the property to them all. You can use a modification to add the fields to all objects.

Step 1: Create Common Interface

Create an interface that all inheriting classes will implement:

.. code-block:: java

    public interface Promotable extends Recordable {

    }

Step 2: Create Modification Class

In the interface, create a static class to contain the modification class properties. In this example, a promo title and image will be added to any classes implementing the Promotable interface. The name of the@BeanProperty path is used when accessing these properties:

.. code-block:: java

    public interface Promotable extends Recordable {

        @BeanProperty("data")
        public static class Data extends Modification<Promotable> {

            @ToolUi.Tab("Promo Overrides")    
            private String promoTitle;
            private Image promoImage;

            // Getters and Setters
        }
    }

Step 3: Implement Modification

Add interface implementation to any classes that should be Promotable:

.. code-block:: java

    public class BlogPost extends Content implements Promotable {

        private String title;
        private ReferentialText bodyText;

        // Getters Setters

    }

Accessing Modification Fields

In the example above, a new promoTitle and promoImage can be added to the objects implementing the interface. To access these fields when rendering the content the @BeanProperty("data") is used:

.. code-block:: jsp

    <cms:render value="${content.data.promoTitle}">
    <cms:img src="${content.data.promoImage}">

Typically a check is made using the ${cms:instanceOf()} tag so objects not being modified and without the fields don't throw errors:

.. code-block:: jsp

    <c:if test="${cms:instanceOf(mainContent,'com.brightspot.Promotable')}">
        <cms:render value="${content.data.promoTitle}">
    </c:if>

Brightspot Object Modifications
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

A good example use case of implementing a Brightspot object modification would be modifiying the Brightspot Site object, to add custom fields. This form of modification allows Brightspot object types to become tailored, without actual customization of the Brightspot code base:

By using Modification.Classes, and then defining the classes that are to inherit, Brightspot object types can be modified from one single class.

Step 1. Implement Modification

.. code-block:: java

    @Modification.Classes({Site.class})
    public class SiteModification extends Modification<Object> {

        private String customSiteField;

        // Getters Setters

    }

Step 2: Return the Class with Modifications

After the modification class has been created, objects of the class being modified can be converted into objects of the modification class. Using the Site.java class example, this is done as follows:

.. code-block:: java

   this.as(SiteModification.class).geCustomSiteField();

In the example code above, the Site object is converted to the modified object, and that SiteModification object is used.

