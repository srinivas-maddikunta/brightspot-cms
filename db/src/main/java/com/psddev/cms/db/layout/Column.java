package com.psddev.cms.db.layout;

import java.util.ArrayList;
import java.util.List;

import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Record;

public abstract class Column extends Record {

    protected abstract LayoutNode createLayoutNode(ObjectType type, int width);

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
        protected LayoutNode createLayoutNode(ObjectType type, int width) {
            ObjectField field = new ObjectField(type, null);

            field.setDisplayName(this.getName());
            field.setInternalName(this.getName());
            field.setInternalType(ObjectField.RECORD_TYPE);
            field.getTypes().add(ObjectType.getInstance(Cell.class));

            FieldNode fieldNode = new FieldNode();
            fieldNode.setField(field);
            fieldNode.setWidth(width);
            return fieldNode;
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
        protected LayoutNode createLayoutNode(ObjectType type, int width) {

            ColumnNode columnNode = new ColumnNode();
            columnNode.setWidth(width);

            for (RowDefinition rowDefinition : getRowDefinitions()) {
                rowDefinition.getState().getExtras().put("isEmbedded", true);
                LayoutNode node = rowDefinition.createLayoutNode(type);
                if (node != null) {
                    node.setParent(columnNode);
                    columnNode.getChildNodes().add(node);
                }
            }

            return columnNode;
        }
    }
}
