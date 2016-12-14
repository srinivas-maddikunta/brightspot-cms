######################
Creating Content Types
######################

The basic elements of Brightspot are the data model and the content types that are managed in the user interface and stored in the database. These content types are created as standard Java classes. The distinction between a standard Java class and one that is managed and stored in Brightspot is the extension of a parent class, Content.

::

    public class MyClass extends Content {


    }

By extending Content, Brightspot will create a user interface to represent the Class and persist the object in the database as a Content Type.

All Content in Brightspot extends the Dari Record class com.psddev.dari.db.Record. Objects are not mapped to database tables, but instead are serialized into JSON and stored in the database as a BLOB, freeing developers from worrying about creating or altering database tables and allowing for rapid evolution of data models.

The properties and data stored in Content Types are defined by standard Java objects as well as several provided by Brightspot and Dari. All of the types that can be added have a corresponding user interface element that is automatically created.

.. code-block:: java

    public class MyClass extends Content {

        // Simple String field
        private String name;
        // Standard Java List
        private List<String> listItems;
        // Date Widget
        private Date datePicker;
        // Brightspot Rich Text Editor
        private ReferentialText bodyText;
        // File Storage
        private StorageItem file;

        // Getters and Setters

    }

.. note::

    Full documentation on available Field Types can be found in the Field Types documentation.

You can structure and reference Content Types in each other and create a data model:

.. code-block:: java    

    public class MyClass extends Content {

        private String name;
        private AnotherClass otherClass;

        // Getters and Setters

    }

.. code-block:: java

    public class AnotherClass extends Content {

        private String name;
        private List<DifferentClass> differentClasses;

        // Getters and Setters

    }


.. code-block:: java        

    public class DifferentClass extends Content {

        private String name;
        private MyClass myOtherClass;

        // Getters and Setters

    }

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/d8606bf/2147483647/resize/700x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2F04%2Fa7%2F0c8ef353479f9ee4e64d3c07dc71%2Fscreen-shot-2014-12-03-at-124425-pmpng.44.25%20PM.png

You can access the created Content Types using the Dari Developer Tools by navigating to http://localhost:8080/_debug/db-schema. Select a created Content Type to view a schema of the relationships and properties in the class:

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/b6b9a3f/2147483647/resize/700x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2F39%2Fd5%2F7e2ca5504d3abdf7195433e3e53b%2Fscreen-shot-2014-11-07-at-125639-pmpng.56.39%20PM.png

You can use the Query Tool to view the data stored in the database. Select ObjectType to view all Content Types, or select a specific Content Type to view all instances of it. Click on an individual instance to show the JSON stored in the database for the object.

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/19a195c/2147483647/resize/700x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2Fc1%2Fe4%2F2298fb4f406090ba2d2212498f0e%2Fscreen-shot-2014-11-07-at-10537-pmpng.05.37%20PM.png

All fields in a class that extends Content or Record are persisted to the database when the object is saved.

Developers are given complete control over how their data is stored and they can change this at any time, without database updates.