Using Annotations
-----------------

Brightspot uses two types of annotations to describe how a field or model class should behave: field annotations and class annotations. Certain annotations can only be applied to a class, others can only be used on fields, and some may be applied to both. To use a field annotation, the annotation can be placed above the target field or beside the field declaration:

An example of the annotation above the field:

.. code-block:: java

    @Required
    private String name;

An example of the annotation beside the field declaration:

.. code-block:: java

    @Required private String name;

Other fields following the target field will not have the annotation.

To use a class annotation, the annotation is placed above the class declaration:

.. code-block:: java

    @ToolUi.GlobalFilter
    public class Article extends Content {

    }
    
Brightspot uses annotations within the Java class to provide control over data indexing, user actions (validation, maximum and minimum list sizes), the user interface (ordering and layout of fields), and editorial instructions (help text and notes).

You can see a list of all annotations and which specific annotations are applied to your content by clicking on the  icon in the top right corner of the publishing widget, opening up a drop-down menu with two tabs: For Editors and For Developers. The developers tab lists present and possible annotations and other information relevant to the raw data of the content type. Click on any annotation link to see the JavaDoc.

.. image:: http://d3qqon7jsl4v2v.cloudfront.net/d1/df/c907cb5c4452995d927295c7dec6/screen-shot-2016-03-30-at-2.00.19%20PM.jpg