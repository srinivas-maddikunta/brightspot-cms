##################
What is Brightspot
##################

****************
About Brightspot
****************

Brightspot is an enterprise user experience platform designed to power dynamic consumer experiences that are editorially rich and visually stunning. Brightspot brings teams together and streamlines daily design, development, and editorial processes.

**Unleash designers:** Create visually stunning experiences with complete design ﬂexibility and no platform constraints.

**Empower developers:** Speed up development, automate routine tasks, and incorporate industry best practices into your existing workflows with the Dari framework toolset.

**Inspire editors:** Make the publishing tool as compelling and as easy-to-use as the experience it powers.

**Enable collaboration:** Streamline teamwork with rapid prototyping, instant changes, and automated tool chains.

********************
How Brightspot Works
********************

Brightspot was built using the `Dari Framework <http://www.dariframework.org>`_, a powerful object persistence library that makes it easy to build complex content types and persist them to one or more database backends. Brightspot supports any operating system that runs Java, all recent versions of Chrome, Firefox, and Safari, and Internet Explorer 11 or better.

Because Brightspot enables the creation of complex data models, it can be used as a publishing platform for websites and native mobile applications, an application back-end, a Customer Relationship Management solution, and much more. Using simple annotations and standard Java types, anything can be modeled and accessed with a web view or through an API.

A simple Brightspot content model might look like this:

.. code-block:: java

    public class MyClass extends Content {

        private String textField;
        private Date dateWidget;
        private List<String> textList;

    }

Brightspot automatically builds a user interface for publishing data. New fields appear as you add them, ready for your data. Brightspot also comes with a library of field types for your convenience.

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/30c3733/2147483647/resize/700x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2F25%2F8f%2Feb630e7b4270a072e6b35c1d317d%2Fscreen-shot-2014-12-03-at-120246-pmpng.32.11%20PM.png

You can use Brightspot to build any type of Java application. The difference between a standard Java object and Brightspot is the extension of :code:`Content`, a parent class provided by Brightspot:

.. code-block:: java

    public class MyClass extends Content {

        private String textField;
        private Date dateWidget;
        private List<String> textList;

    }

By extending Content, Brightspot knows to create a user interface with the Class, and persist the object in the database as a Content Type, as well as any instances of it.

All Content in Brightspot extends the Dari Record class: com.psddev.dari.db.Record. Objects are not mapped to database tables, but instead are serialized into JSON and stored in the database as JSON, freeing developers from creating and altering database tables and allowing you to quickly evolve data models.

::

    {
        "textField" : "Create a user interface automatically",
        "dateWidget" : 1393688100000,
        "textList"; : [ "Standard Java" ],
    }        

All fields in a class that extends Content / Record are persisted to the database when the object is saved.

You have complete control over how your data is stored. You can make changes at any time, without updating the database.

Here is an example data model of an Author content type:

.. code-block:: java

    public class Author extends Content {

        private String name;
        private String bio;

        // Getters and Setters
    }

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/8212567/2147483647/resize/700x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2Fb1%2F89%2F249636264cf896aa62aca89404fc%2Fscreen-shot-2014-12-03-at-121112-pmpng.33.29%20PM.png

In Brightspot, the Author content type can immediately be created, saved, drafted, scheduled, and searched. It can be versioned, changes in the content between versions can be tracked, and roles and permissions for access to the creation of an Author can be applied.

Brightspot uses Dari to save objects to two databases: SQL and Solr.

***************************
How Brightspot is Different
***************************

Because content types are custom-created using Java classes, each instance of Brightspot is tailored to the user. Labels, terms, and content types are all based on the application being created. The platform provides features that every application can use, such as search, scheduling, and publishing to the web. Brightspot doesn't dictate what you manage in the platform, or how you present it on the web.

***********
Get Started
***********

The Brightspot stack consists of proven open-source software: Java, MySQL, Solr and Apache httpd. No proprietary technology is used. Brightspot is open-source and freely available for use under the GNU GENERAL PUBLIC LICENSE V2. Brightspot supports any SQL server via JDBC, including MySQL, Postgres, and Oracle.

Because Brightspot applications are built with standard Java development techniques, Java developers can get started with Brightspot development quickly and easily.

Start using Brightspot by `installing the platform <http://www.brightspot.com/docs/3.2/getting-started/installation>`_.