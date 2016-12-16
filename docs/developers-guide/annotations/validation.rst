Validation
----------

Annotations designed to help with validation on publish can help you create content that will work based on front-end limitations such as required fields, maximum character length, or list size.

To specify a field as being required, add @Required, or, to make sure it remains unique, add @Indexed(unique=true). To control the size of lists, use @CollectionMaximum or @CollectionMinimum. To set a soft limit on characters, which offers the user a Too Long pop-up, add @ToolUi.FieldSuggestedMaximum.

.. code-block:: java

    public class Athlete extends Content {

        @Indexed(unique=true)
        private String name;
        @Required
        private String age;
        @CollectionMaximum(5)
        @CollectionMinimum(2)
        private List<Sport> sports;
        @ToolUi.FieldSuggestedMaximum(250)
        private String bio;

        // Getters and Setters

    }

**@Maximum(double)**

Specifies either the maximum numeric value or string length of the target field. The @Maximum annotation is used to control the size of list fields. In the example below, the list of articles is limited to a maximum of 4:

.. code-block:: java

    public class RSSFeed extends Content {

        @Maximum(4)
        private List<Article> articles;

    }

**@Minimum(double)**

Specifies either the minimum numeric value or string length of the target field. The @Minimum annotation is used to control the size of list fields. In the example below, the list of articles must contain at least one article:

.. code-block:: java

    public class RSSFeed extends Content {

        @Minimum(1)
        private List<Article> articles;

    }

**@Regex(String)**

Specifies the regular expression pattern that the target field value must match. This annotation is typically used on fields. For example:

.. code-block:: java

    public class Author extends Content {

        private String name;

        @Recordable.Regex("(^.*)([a-f||\\d]{8}-[a-f||\\d]{4}-[a-f||\\d]{4}-[a-f||\\d]{4}-[a-f||\\d]{12})$")
        private String email;

    }

**@Required**

Specifies whether the target field value is required for the user. For example, the headline field is required for an Article:

.. code-block:: java

    public class Article extends Content {

        @Required
        private String headline;

        // Getters and Setters
        @Step(double)

Specifies the step between the minimum and the maximum that the target field must match. This annotation is typically applied to fields. Using the example of the list of Articles, the step between the minimum and maximum number of Articles is 1.

.. code-block:: java

    public class RSSFeed extends Content {

        @Minimum(1) 
        @Step(1)
        @Maximum(4)
        private List<Article> articles;

    }

**@Types(Class[])**

Specifies the valid types for the target field value. @Types({Image.class, Video.class, Widget.class}). The annotation is applied to fields. In the example below, the valid content types to be added to the list of items are Image and Video:

.. code-block:: java

    public class Gallery extends Content {

        @Types(Image.class, Video.class)
        private List<media> items;

    }

**@Values(String[])**

Specifies the valid values for the target field value. The annotation is applied to fields. In the example below, the valid values to select in the team color field are red, blue, yellow, and green:

.. code-block:: java

    public class Team extends Content {

        private String teamName; 

        @Values(Red, Blue, Yellow, Green)
        private String teamColor;

    }