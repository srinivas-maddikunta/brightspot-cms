Data
----

Use the @Indexed annotation to define data for users to query. Adding the @Indexed annotation to all the fields on every class creates potentially unnecessary rows in the underlying database and can lead to poor performance in systems with large amounts of data. The annotation should only be added to fields that will be queried. Fields can be returned and rendered without indexing, but to query on a field, it must be indexed. In the example below, to query for all Articles where the headline starts with 'A', the field would need to be @Indexed.

.. code-block:: java

    public class Article extends Content {

        @Indexed
        private String headline;
        private ReferentialText body;
        private List<Tag> tags;
        private Set<String> keywords;

        // Getters and Setters

    }
    
You can index content after creation by adding the @Indexed annotation to the field and reindexing the content using the _debug/db-bulk tool.

**@Abstract**

Specifies that the target can't be used to create a concrete instance. In the example below, the @Abstract annotation is used on the AbstractArticle class:

.. code-block:: java

    @Abstract
    public abstract class AbstractArticle extends Content{

    }

**@BeanProperty**

Specifies the JavaBeans property name used to access an instance of the target type as a modification.

.. code-block:: java

    public interface Promotable extends Recordable {

        @BeanProperty("data")
        public static class Data extends Modification<promotable> {

            @ToolUi.Tab("Promo Overrides")    
            private String promoTitle;
            private Image promoImage;

            // Getters and Setters
        }
    }

Can be accessed using:

.. code-block:: jsp

    <cms:render value="${content.data.promoTitle}">
    <cms:img src="${content.data.promoImage}">

**@Denormalized**

Denotes that the target field is always denormalized within another instance. When @Denormalized is used on a class that is referenced by another class, it 'de-normalizes', or copies, the data to the referring class. You can use it in site searches. The denormalized data is saved in the solr Index and is not visible on the object. For example, there are two classes: Person and State, where Person has a reference field State on it. Perform a people search to include the State name:

.. code-block:: java

    public class State extends Content {

        @Indexed (unique = true)
        private String name;
        @Indexed (unique = true)
        @Denormalized
        private String abbreviation;
    }

.. code-block:: java

    public class Person extends Content {

        @Indexed
        private String firstName;
        @Indexed
        private String lastName;
        @Indexed
        private UsState usState;
    }

To perform a people search to include the State name:

.. code-block:: java

    Search search = new Search();
    search.addTypes(Person.class);
    search.toQuery("va").select(0, 5);

Boost the search by the denormalized abbreviation value:

.. code-block:: java

    search.boostFields(5.0, Person.class, "usState/abbreviation");

Only use this annotation when necessary in advanced cases.

**@DisplayName(String)**

Specifies the target type's display name. Use the @DisplayName annotation on a field to display a different field name on the front end. For example:

.. code-block:: java

    public class Article extends Content {

        @Recordable.DisplayName("Short Headline")
        private String summary;

    }

**@Embedded**

Specifies whether the target type data is always embedded within another type data. The @Embedded annotation applies to both classes and fields. To embed an object within an object, use the annotation @Embedded with a static class. An example applied to a class:

.. code-block:: java

    public class Company extends Content {

        private String name;
        private Contact contact;

        @Embedded
        public static class Contact extends Content {

            private String address;

        }

    }

An example applied to a field:

.. code-block:: java

    public class Company extends Content {

        @Embedded
        private Contact contact;


    }

**@InternalName(String)**

Specifies the target type's internal name. The @InternalName annotation applies to both classes and fields. An example applied to a class:

.. code-block:: java

    @InternalName("Old")
    public class MigratedBlogPost extends Content {

    }

An example applied to a field:

.. code-block:: java

    public class BlogPost extends Content {

        @InternalName("oldBlogTitle")
        private Title oldTitle;

    }

**@LazyLoad**

An internal annotation to denote that the fields in the target type are lazily loaded. The @LazyLoad annotation is applied to a class. For example, images may be lazy loaded to facilitate smooth browsing through a photo gallery with several images.

**@Recordable.JunctionField**

Specifies the name of the field in the junction query that should be used to populate the target field. Given two objects, Video and Playlist, JunctionField would allow the population of a list of videos for a playlist, on the Playlist object, by having each Video object contain a reference to the Playlist it is in.

Video object:

.. code-block:: java

    public class Video extends Content { 

        private String name;
        private StorageItem videoFile;

        @Indexed 
        private Playlist playlist; 
    }

The Playlist object:

.. code-block:: java

    public class Playlist extends Content { 

        private String name; 

        @Indexed 
        @Recordable.JunctionField("playlist") 
        private List<Video> videos; 
    }

**@Recordable.JunctionPositionField**

Specifies the name of the position field in the junction query that should be used to order the collection in the target field.

**@Recordable.LabelFields(String[])**

Specifies the field names used to retrieve the labels of the objects represented by the target type. The @LabelFields annotation is used on a class. In the example below, the name field is used to retrieve the object labels:

.. code-block:: java

    @Recordable.LabelFields("name")
    public class Author extends Content {

        @Required
        private String name;

    }

**@Recordable.MetricValue**

Specifies the field in which the metric is recorded. This annotation is only applicable to Metric fields. It allows you to specify which MetricInterval to use when storing Metric values. The default is MetricInterval.Hourly.class, so this annotation is optional.

Example: Use an interval of None to eliminates the time series component of the Metric value.

.. code-block:: java

    @MetricValue(interval = com.psddev.dari.db.MetricInterval.None.class)
    Metric myMetric;

You can also reference a setting key that holds the name of the class to use:

.. code-block:: java

    @MetricValue(intervalSetting = "analytics/metricIntervalClass")
    Metric myMetric;

Update Context.xml as follows:

.. code-block:: xml

    <environment name="analytics/metricIntervalClass" type="java.lang.String" value="com.psddev.dari.db.MetricInterval$Minutely" override="false" />

**@Recordable.PreviewField**

Specifies the field name used to retrieve the previews of the objects represented by the target type. This annotation is typically used on image/video classes. For example, when @PreviewField is used on an image object, the file field name is used to retrieve the preview:

.. code-block:: java

    @Recordable.PreviewField("file")
    public class Image extends Content {

        private String name;
        private StorageItem file;
        private String altText;

    }

**@SourceDatabaseClass**

Specifies the source database class for the target type. The annotation is applied to a class.

**@Recordable.SourceDatabaseName**

Specifies the source database name for the target type. The annotation is applied to a class.