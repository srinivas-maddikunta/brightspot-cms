package com.psddev.cms.db;

import java.util.Map;

import com.psddev.dari.db.Record;
import com.psddev.dari.util.CompactMap;

@ToolUiLayoutElement.Embedded
public class ToolUiLayoutElement extends Record {

    private String name;
    private double left;
    private double width;
    private int top;
    private int height;
    private String dynamicText;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getDynamicText() {
        return dynamicText;
    }

    public void setDynamicText(String dynamicText) {
        this.dynamicText = dynamicText;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new CompactMap<String, Object>();

        map.put("name", getName());
        map.put("left", getLeft());
        map.put("top", getTop());
        map.put("width", getWidth());
        map.put("height", getHeight());
        map.put("dynamicText", getDynamicText());

        return map;
    }
}
