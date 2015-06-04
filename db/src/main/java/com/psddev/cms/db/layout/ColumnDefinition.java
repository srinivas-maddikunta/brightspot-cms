package com.psddev.cms.db.layout;

import com.psddev.cms.db.ToolUi;
import com.psddev.cms.db.ToolUiLayoutElement;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Record;

public class ColumnDefinition extends Record {

    private String name;
    private int height;
    private int width;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public ObjectField createColumnField(ObjectType type, String fieldName, int topOffset, int leftOffset) {

        ObjectField field = new ObjectField(type, null);

        field.setDisplayName(fieldName);
        field.setInternalName(fieldName);
        field.setInternalType(ObjectField.RECORD_TYPE);
        field.getTypes().add(ObjectType.getInstance(Cell.class));

        ToolUiLayoutElement layoutElement = new ToolUiLayoutElement();
        layoutElement.setTop(topOffset);
        layoutElement.setLeft(leftOffset);
        layoutElement.setWidth(width);
        layoutElement.setHeight(height);

        field.as(ToolUi.class).setLayoutField(layoutElement);

        return field;
    }
}
