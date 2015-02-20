package com.psddev.cms.tool.page;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;

import com.psddev.cms.db.ToolUser;
import com.psddev.cms.tool.Dashboard;
import com.psddev.cms.tool.DashboardColumn;
import com.psddev.cms.tool.DashboardWidget;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.RoutingFilter;

@RoutingFilter.Path(application = "cms", value = "/misc/updateUserDashboard")
public class UpdateUserDashboard extends PageServlet {

    @Override
    protected String getPermissionId() {
        return "area/dashboard";
    }

    @Override
    protected void doService(ToolPageContext page) throws IOException, ServletException {
        reallyDoService(page);
    }

    protected static void reallyDoService(ToolPageContext page) throws IOException, ServletException {
        if (!page.isFormPost()) {
            throw new IllegalStateException("Form must be posted!");
        }

        for (String action : page.params(String.class, "action")) {
            switch (action) {
                case "dashboardWidgets-add" :
                    addWidget(page);
                    break;
                case "dashboardWidgets-remove" :
                    removeWidget(page);
                    break;
                case "dashboardWidgets-move" :
                    moveWidget(page);
                    break;
                case "dashboardColumns-add" :
                    addColumn(page);
                    break;
                case "dashboardColumns-resize" :
                    break;
                default :
                    return;
            }
        }
    }

    private static void addWidget(ToolPageContext page) {

        ToolUser user = page.getUser();
        Dashboard dashboard = getDashboard(page);

        int columnIndex = page.param(int.class, "col");
        List<DashboardColumn> columns = dashboard.getColumns();

        Object widget = page.getRequest().getAttribute("widget");

        if (widget == null) {
            throw new IllegalArgumentException("No widget found on request");
        }

        columns.get(columnIndex).getWidgets().add((DashboardWidget) widget);
        user.setDashboard(dashboard);
        user.save();
    }

    private static void moveWidget(ToolPageContext page) {

        ToolUser user = page.getUser();
        Dashboard dashboard = getDashboard(page);

        int targetX = page.param(int.class, "targetX");
        int targetY = page.param(int.class, "targetY");
        int originalX = page.param(int.class, "originalX");
        int originalY = page.param(int.class, "originalY");
        UUID widgetId = page.param(UUID.class, "id");

        if (widgetId == null) {
            throw new IllegalArgumentException("id is a required parameter");
        }

        List<DashboardColumn> columns = dashboard.getColumns();
        DashboardWidget movedWidget = columns.get(originalY).getWidgets().get(originalX);
        columns.get(originalY).getWidgets().remove(originalX);
        columns.get(targetY).getWidgets().add(targetX, movedWidget);

        user.setDashboard(dashboard);
        user.save();
    }

    private static void removeWidget(ToolPageContext page) {
        return;
    }

    private static void addColumn(ToolPageContext page) {
        return;
    }

    private static Dashboard getDashboard(ToolPageContext page) {

        Dashboard dashboard = page.getUser().getDashboard();

        if (dashboard == null) {
            dashboard = page.getCmsTool().getDefaultDashboard();

            if (dashboard == null) {
                dashboard = Dashboard.getDefaultDashboard();
            }
        }

        List<DashboardColumn> columns = dashboard.getColumns();

        if (ObjectUtils.isBlank(columns)) {
            columns.add(new DashboardColumn());
            dashboard.setColumns(columns);
        }

        return dashboard;

    }
}
