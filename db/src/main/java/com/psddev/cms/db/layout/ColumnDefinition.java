package com.psddev.cms.db.layout;

import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Record;

/**
 * Allows creation of editorially defined
 * column {@link com.psddev.dari.db.ObjectField}s for a {@link Row}.
 */
public class ColumnDefinition extends Record {

    private int width;

    @Required
    @Embedded
    private Column column;

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public LayoutNode createLayoutNode(ObjectType objectType) {
        Column column = this.getColumn();

        if (column == null) {
            return null;
        }

        return column.createLayoutNode(objectType, width);
    }
}
