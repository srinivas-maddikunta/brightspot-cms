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

Looking at the Raw Data of the object shows the internalName field populated in a normalized way.

.. image:: http://cdn.brightspotcms.psdops.com/dims4/default/bcf9eb6/2147483647/resize/700x/quality/90/?url=http%3A%2F%2Fd3qqon7jsl4v2v.cloudfront.net%2F47%2F32%2F41209dac482083f9d415fda47e7d%2F75-68png.png

While saving an object, it is also possible to throw a field exception if it fails to meet validation. For example, the name field of an object is required. If the error is found during validation before saving, the field is highlighted and the error message displays above the field.

.. code-block:: java

    @Override
    protected void beforeSave() {
        getState().addError(getState().getField("fieldNameToValidate"), "Error message here.");
    }

After Save
~~~~~~~~~~

The afterSave method specifies actions to be performed after the record is saved, and is often used to log data or send notification once the record has been saved. In the example below, after saving the record, the name field entered by the editor is saved as internalName instead of the normalized value:

.. code-block:: java

    public void afterSave() {
        this.internalName = name;
    }


Before Delete
~~~~~~~~~~~~~

The beforeDelete method specifies actions to be performed before the record is deleted, and is often used to verify and validate data before the record is deleted. For example, using the method to check if the record is not null before deleting it.