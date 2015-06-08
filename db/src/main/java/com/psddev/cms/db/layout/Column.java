package com.psddev.cms.db.layout;

import java.util.ArrayList;
import java.util.List;

import com.psddev.cms.db.ToolUi;
import com.psddev.cms.db.ToolUiLayoutElement;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Record;

public abstract class Column extends Record {

    abstract List<ObjectField> createFields(ObjectType type, int width, int topOffset, int leftOffset);

    static void setLayoutField(ObjectField field, int width, int topOffset, int leftOffset) {
        ToolUiLayoutElement layoutElement = new ToolUiLayoutElement();
        layoutElement.setTop(topOffset);
        layoutElement.setLeft(leftOffset);
        layoutElement.setWidth(width);
        layoutElement.setHeight(1);
        field.as(ToolUi.class).setLayoutField(layoutElement);
    }

    public static class Cell extends Column {
        @Required
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        List<ObjectField> createFields(ObjectType type, int width, int topOffset, int leftOffset) {
            ObjectField field = new ObjectField(type, null);

            field.setDisplayName(this.getName());
            field.setInternalName(this.getName());
            field.setInternalType(ObjectField.RECORD_TYPE);
            field.getTypes().add(ObjectType.getInstance(Cell.class));
            setLayoutField(field, width, topOffset, leftOffset);

            List<ObjectField> fields = new ArrayList<>();
            fields.add(field);
            return fields;
        }

    }

    public static class Container extends Column {

        @Embedded
        @Minimum(1)
        private List<RowDefinition> rowDefinitions;

        public List<RowDefinition> getRowDefinitions() {
            if (rowDefinitions == null) {
                rowDefinitions = new ArrayList<>();
            }
            return rowDefinitions;
        }

        public void setRowDefinitions(List<RowDefinition> rowDefinitions) {
            this.rowDefinitions = rowDefinitions;
        }

        @Override
        List<ObjectField> createFields(ObjectType type, int width, int topOffset, int leftOffset) {

            List<ObjectField> newFields = new ArrayList<>();

            for (RowDefinition rowDefinition : getRowDefinitions()) {
                List<ObjectField> rowFields = rowDefinition.createFields(type, topOffset, leftOffset);

                for (ObjectField field : rowFields) {
                    setLayoutField(field, width, topOffset, leftOffset);
                    topOffset += 1;
                }

                newFields.addAll(rowFields);
            }

            return newFields;
        }
    }
}
