<%@ page session="false" import="com.psddev.cms.tool.page.FileSelector, com.psddev.cms.tool.ToolPageContext" %>

<div class="inputSmall">
    <% FileSelector.reallyDoService(new ToolPageContext(pageContext)); %>
</div>