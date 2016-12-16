Custom Site Settings
--------------------

The default Site class has several site specific options like dashboard config, Brightspot logo, and default accesible sites. A modifcation on the Brightspot Site object can be added if you need new custom settings:

.. code-block:: java

    @Modification.Classes({Site.class})
    public class SiteModification extends Modification<Object> {

        @ToolUi.Tab("Custom Site Settings")
        private String customSiteField;


        public String getCustomSiteField(){
            return customSiteField;
        }

        public void setCustomSiteField(String customSiteField){
            this.customSiteField = customSiteField;
        }

    }