Inheritance
-----------

Parent Child
~~~~~~~~~~~~

Inheritance of classes works in Brightspot as expected in Java. The user interface updates to show inherited fields and the query API supports the querying of the parent to return children.

By creating an Abstract parent classes they are not made available in Brightspot for editorial control.

.. code-block:: java

    public abstract class BaseArticle extends Content {

        private String headline;
        private Author author;
        private ReferentialText bodyText;

        // Getters and Setters
    }

The child class will inherit all of the BaseArticle fields in the user interface.

.. code-block:: java

    public class NewsArticle extends BaseArticle {

        private List<String> newsKeywords;
        private Source newsSource;

        // Getters and Setters
    }

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/e4d28cc/2147483647/resize/700x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2Fdc%2F05%2F943dea40468f98806c9bc2befe82%2Fscreen-shot-2014-11-25-at-113210-ampng.32.10%20AM.png

The Dari db-schema tool shows the inheritance of each type:

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/1852bc6/2147483647/resize/700x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2Ffd%2F27%2F07ca11c8475480c5f40fa2dcfd4c%2Fscreen-shot-2014-11-25-at-113447-ampng.34.47%20AM.png

Junction Field
~~~~~~~~~~~~~~

The @JunctionField annotation can help with many to many relationships in Brightspot.

This annotation specifies the name of the field in the junction query that should be used to populate the target field. Given two objects, Video and Playlist, JunctionField would allow the population of a list of videos for a playlist on the Playlist object by having each Video object contain a reference to the Playlist it is in.

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

The @JunctionPositionField annotation specifies the name of the position field in the junction query that should be used to order the collection in the target field.