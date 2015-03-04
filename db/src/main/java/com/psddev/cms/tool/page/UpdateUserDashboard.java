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
                    resizeColumns(page);
                    break;
                default :
                    return;
            }
        }
    }

    /**
     * Adds a widget to a user's dashboard,
     * also conditionally adds a column
     * @param page
     */
    private static void addWidget(ToolPageContext page) {

        ToolUser user = page.getUser();
        Dashboard dashboard = getDashboard(page);

        int columnIndex = page.param(int.class, COLUMN_INDEX_KEY);
        int rowIndex = page.param(int.class, ROW_INDEX_KEY);
        boolean shouldAddColumn = page.param(boolean.class, "addColumn");
        List<DashboardColumn> columns = dashboard.getColumns();
        DashboardColumn column;

        if (shouldAddColumn) {
            column = new DashboardColumn();
            column.setSize(1);
            columns.add(columnIndex, column);
        } else {
            column = columns.get(columnIndex);
        }

        Object widget = page.getRequest().getAttribute("widget");

        if (widget == null) {
            throw new IllegalArgumentException("No widget found on request");
        }

        List<DashboardWidget> widgets = column.getWidgets();

        if (rowIndex >= widgets.size()) {
            widgets.add((DashboardWidget) widget);
        } else {
            widgets.add(rowIndex, (DashboardWidget) widget);
        }

        user.setDashboard(dashboard);
        user.save();
    }

    /**
     * Moves a widget on a user's dashboard
     * @param page
     */
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

    /**
     * Removes a widget from the user's dashboard
     * @param page
     */
    private static void removeWidget(ToolPageContext page) {

        ToolUser user = page.getUser();
        Dashboard dashboard = getDashboard(page);

        int row = page.param(int.class, ROW_INDEX_KEY);
        int col = page.param(int.class, COLUMN_INDEX_KEY);

        dashboard.getColumns().get(col).getWidgets().remove(row);
        user.setDashboard(dashboard);
        user.save();
    }

    /**
     * Adds a column to the user's dashboard
     * @param page
     */
    private static void addColumn(ToolPageContext page) {

        ToolUser user = page.getUser();
        Dashboard dashboard = getDashboard(page);

        int col = page.param(int.class, COLUMN_INDEX_KEY);
        dashboard.getColumns().add(col, new DashboardColumn());

        user.setDashboard(dashboard);
        user.save();
    }

    private static void resizeColumns(ToolPageContext page) {
        ToolUser user = page.getUser();
        Dashboard dashboard = getDashboard(page);

        List<Integer> columnIndexes = page.params(int.class, COLUMN_INDEX_KEY);
        List<Integer> sizes = page.params(int.class, "size");

        if (ObjectUtils.isBlank(columnIndexes) || ObjectUtils.isBlank(sizes)) {
            return;
        }

        List<DashboardColumn> columns = dashboard.getColumns();

        for (int i : columnIndexes) {
            columns.get(i).setSize(sizes.get(i));
        }

        user.setDashboard(dashboard);
        user.save();
    }

    /**
     * Gets a dashboard for the user to edit
     * @param page
     * @return Dashboard
     */
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
