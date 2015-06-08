package com.psddev.cms.db.layout;

import java.util.List;

import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Record;

/**
 * Allows creation of editorially defined
 * column {@link ObjectField}s for a {@link Row}.
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

    public List<ObjectField> createFields(ObjectType objectType, int topOffset, int leftOffset) {
        Column column = this.getColumn();

        if (column == null) {
            return null;
        }

        return column.createFields(objectType, width, topOffset, leftOffset);
    }
}
