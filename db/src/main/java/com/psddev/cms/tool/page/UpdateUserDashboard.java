package com.psddev.cms.tool.page;

import java.io.IOException;
import java.util.List;

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

    private static final String COLUMN_INDEX_KEY = "y";
    private static final String ROW_INDEX_KEY = "x";

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

        int columnIndex = page.param(int.class, COLUMN_INDEX_KEY);
        int rowIndex = page.param(int.class, ROW_INDEX_KEY);
        List<DashboardColumn> columns = dashboard.getColumns();

        Object widget = page.getRequest().getAttribute("widget");

        if (widget == null) {
            throw new IllegalArgumentException("No widget found on request");
        }

        List<DashboardWidget> widgets = columns.get(columnIndex).getWidgets();

        if (rowIndex >= widgets.size()) {
            widgets.add((DashboardWidget) widget);
        } else {
            widgets.add(rowIndex, (DashboardWidget) widget);
        }

        user.setDashboard(dashboard);
        user.save();
    }

    private static void moveWidget(ToolPageContext page) {

        ToolUser user = page.getUser();
        Dashboard dashboard = getDashboard(page);

        int targetX = page.param(int.class, ROW_INDEX_KEY);
        int targetY = page.param(int.class, COLUMN_INDEX_KEY);
        int originalX = page.param(int.class, "originalX");
        int originalY = page.param(int.class, "originalY");

        List<DashboardColumn> columns = dashboard.getColumns();
        DashboardWidget movedWidget = columns.get(originalY).getWidgets().get(originalX);
        columns.get(originalY).getWidgets().remove(originalX);
        columns.get(targetY).getWidgets().add(targetX, movedWidget);

        user.setDashboard(dashboard);
        user.save();
    }

    private static void removeWidget(ToolPageContext page) {
        int row = page.param(int.class, ROW_INDEX_KEY);
        int col = page.param(int.class, COLUMN_INDEX_KEY);
        ToolUser user = page.getUser();
        Dashboard dashboard = getDashboard(page);
        dashboard.getColumns().get(col).getWidgets().remove(row);
        user.setDashboard(dashboard);
        user.save();
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
