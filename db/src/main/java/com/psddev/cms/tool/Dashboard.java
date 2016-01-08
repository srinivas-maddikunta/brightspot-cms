package com.psddev.cms.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.psddev.dari.db.Record;
import com.psddev.dari.util.ClassFinder;
import com.psddev.dari.util.TypeDefinition;
import com.psddev.dari.util.UuidUtils;
import java8.util.stream.StreamSupport;

@Dashboard.Embedded
public class Dashboard extends Record {

    private static final String WIDGET_ID_NAME_PREFIX = Dashboard.class.getName() + "/";

    private List<DashboardColumn> columns;

    /**
     * Creates a default dashboard containing instances of all classes that
     * implement {@link DefaultDashboardWidget}.
     *
     * @return Never {@code null}.
     */
    public static Dashboard createDefaultDashboard() {
        Dashboard dashboard = new Dashboard();
        List<DashboardColumn> columns = dashboard.getColumns();

        StreamSupport.stream(ClassFinder.findConcreteClasses(DefaultDashboardWidget.class)).forEach(c -> {
            DefaultDashboardWidget widget = TypeDefinition.getInstance(c).newInstance();
            int columnIndex = widget.getColumnIndex();

            widget.getState().setId(UuidUtils.createVersion3Uuid(WIDGET_ID_NAME_PREFIX + c.getName()));

            List<Integer> range = new ArrayList<>();
            for (int i = 0; i <  columnIndex - columns.size() + 1; i++) {
                range.add(i);
            }

            //IntStream.range(0, columnIndex - columns.size() + 1)
            StreamSupport.stream(range)
                    .forEach(i -> columns.add(new DashboardColumn()));

            columns.get(columnIndex)
                    .getWidgets()
                    .add(widget);
        });

        double width = 1.0;

        for (DashboardColumn column : columns) {
            width /= 1.61803398875;

            column.setWidth(width);

            Collections.sort(
                    column.getWidgets(),
                    (w1, w2) -> (((DefaultDashboardWidget) w1).getWidgetIndex() + " " + w1.getClass().getName()).compareTo(((DefaultDashboardWidget) w2).getWidgetIndex() + " " + w2.getClass().getName()));

                    //Comparator.comparingInt(w -> ((DefaultDashboardWidget) w).getWidgetIndex())
                      //      .thenComparing(w -> w.getClass().getName()));
        }

        return dashboard;
    }

    /**
     * @deprecated Use {@link #createDefaultDashboard()} instead.
     */
    @Deprecated
    public static Dashboard getDefaultDashboard() {
        return createDefaultDashboard();
    }

    public List<DashboardColumn> getColumns() {
        if (columns == null) {
            columns = new ArrayList<>();
        }
        return columns;
    }

    public void setColumns(List<DashboardColumn> columns) {
        this.columns = columns;
    }
}
