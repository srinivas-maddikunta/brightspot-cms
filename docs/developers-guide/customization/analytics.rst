Analytics
---------

Add any commonly used analytics provider JavaScript code to a site by creating the appropriate fields in the Admin section of Brightspot to contain the JavaScript code:

Create a SiteSettings class, extending Tool:

.. code-block:: java

    public class SiteSettings extends Tool {

        private String analyticsID;

        public String getAnalyticsID(){
            return analyticsID;
        }

        public void setAnalyticsID(String analyticsID){
            this.analyticsID = analyticsID;
        }

    }

Using the Application Settings
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

For example, you may want to access an Analytics ID application setting for the site in a JSP. As in the example above, add a String field. In your JSP, import the SiteSettings object and the Query library. Query from the SiteSettings object and set the pageContext. Below is an example of a Google Analytics implementation.

.. code-block:: jsp

    <%@page import="com.perfectsensedigital.SiteSettings,com.psddev.dari.db.Query,com.psddev.dari.util.Settings"%>

        <%
        SiteSettings settings = Application.Static.getInstance(SiteSettings.class);
        if (settings != null) {
            pageContext.setAttribute("analyticsID", settings.getAnalyticsID());    }
        %>

    <c:if test="${not empty analyticsID}">
        <script type="text/javascript">

            var _gaq = _gaq || [];
            _gaq.push(['_setAccount', '${analyticsID}']);
            _gaq.push(['_trackPageview']);

            (function() {
                var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
                ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
                var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
            })();

        </script>
    </c:if>