Data Validation
---------------

Before Save
~~~~~~~~~~~

Validation of data within properties can be controlled through annotations in Brightspot. By leveraging the beforeSave() method, data can be validated, modified or thrown out when content is saved. Common uses for beforeSave() include the transformation of data and the population of hidden fields. In the example below, a hidden field called internalName is populated with a normalized version of the name field data added by the editor.

.. code-block:: java

    public class Project extends Content {

        @Required
        private String name;

        @Indexed
        @ToolUi.Hidden
        private String internalName;

        // Getters and Setters

        @Override
        public void beforeSave() {
            this.internalName = StringUtils.toNormalized(name);
        }

        public String toString() {
            return internalName;
        }

    }

Referring to the above class, if a user enters ``Edward R. Murrow`` in the Name field, Brightspot stores the associated normalized name in a hidden internalName field. The following raw data snippet shows the internal name's format.

.. code-block:: json

   {
      "name" : "Edward R. Murrow",
      "internalName" : "edward-r-murrow",
      "cms.content.draft" : true,
      "cms.content.publishDate" : 1484930453100,
      "cms.content.publishUser" : {
         "_ref" : "00000159-b841-d9cb-a359-bef1cc370000",
         "_type" : "00000159-b840-d9cb-a359-bef12aad0027"
       }
   }

While saving an object, it is also possible to throw a field exception if it fails to meet validation. For example, the name field of an object is required. If the error is found during validation before saving, the field is highlighted and the error message displays above the field.

.. code-block:: java

    @Override
    protected void beforeSave() {
        getState().addError(getState().getField("fieldNameToValidate"), "Error message here.");
    }

After Save
~~~~~~~~~~

The afterSave method specifies actions to be performed after the record is saved, and is often used for data logging or sending notifications. In the example below, after saving the record, the name field entered by the editor is saved as internalName instead of the normalized value:

.. code-block:: java

    public void afterSave() {
        this.internalName = name;
    }


Before Delete
~~~~~~~~~~~~~

The beforeDelete method specifies actions to be performed before the record is deleted, and is often used to verify and validate data before the record is deleted. For example, you can use this method to ensure a record is null before deleting it.
