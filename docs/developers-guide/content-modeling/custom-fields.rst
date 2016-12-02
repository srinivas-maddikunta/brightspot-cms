Creating Custom Fields
----------------------

Brightspot allows you to create custom user interfaces. Add the @ToolUi.InputProcessorPath() annotation to any field to access a custom renderer for that field.

.. code-block:: java

    public class Video extends Content {

        private String name;
        private String videoId;
        @ToolUi.InputProcessorPath("/custom/videoPreview.jsp")
        private String videoPreview;

    }
    
Add the annotation @ToolUi.InputSearcherPath() to specify the path to the searcher used to find a value for the target field.