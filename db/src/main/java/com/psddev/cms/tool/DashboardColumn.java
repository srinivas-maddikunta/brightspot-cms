package com.psddev.cms.tool;

import java.util.ArrayList;
import java.util.List;

import com.psddev.dari.db.Record;

@DashboardColumn.Embedded
public class DashboardColumn extends Record {

    private int size;

    @Embedded
    private List<DashboardWidget> widgets;

    public int getSize() {
        return (size == 0) ? 1 : size;
    }

    public void setSize(int size) {
        this.size = size;
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
