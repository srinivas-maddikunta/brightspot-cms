<%@ page session="false" import="com.psddev.cms.tool.ToolPageContext, com.psddev.cms.tool.page.StorageItemField" %>

<%
    ToolPageContext wp = new ToolPageContext(pageContext);
    if (wp.isFormPost()) {
        StorageItemField.doFormPost(wp);
        return;
    }
%>

<div class="inputSmall">
    <% StorageItemField.reallyDoService(wp); %>
</div>