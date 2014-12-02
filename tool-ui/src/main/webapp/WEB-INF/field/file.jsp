<%@ page session="false" import="com.psddev.cms.tool.page.FileSelector, com.psddev.cms.tool.ToolPageContext, com.psddev.dari.util.WebPageContext" %>

<%
    ToolPageContext wp = new ToolPageContext(pageContext);
    if (wp.isFormPost()) {
        FileSelector.doFormPost(wp);
        return;
    }
%>

<div class="inputSmall">
    <% FileSelector.reallyDoService(wp); %>
</div>