package com.psddev.cms.tool;

import java.util.ArrayList;
import java.util.List;

import com.psddev.dari.db.Record;

@DashboardColumn.Embedded
public class DashboardColumn extends Record {

    public static final int MINIMUM_WIDTH = 320;

    private int width;

    @Embedded
    private List<DashboardWidget> widgets;

    public int getWidth() {
        return (width < MINIMUM_WIDTH) ? MINIMUM_WIDTH : width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public List<DashboardWidget> getWidgets() {
        if (widgets == null) {
            widgets = new ArrayList<>();
        }
        return widgets;
    }

    public void setWidgets(List<DashboardWidget> widgets) {
        this.widgets = widgets;
    }

    @Override
    public String getLabel() {
        return getWidgets().size() + " Widgets";
    }
}
