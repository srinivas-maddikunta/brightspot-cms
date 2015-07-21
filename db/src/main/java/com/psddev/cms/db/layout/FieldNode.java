package com.psddev.cms.db.layout;

import com.psddev.cms.db.ToolUi;
import com.psddev.cms.db.ToolUiLayoutElement;
import com.psddev.dari.db.ObjectField;

/**
 * {@code FieldNode} is a leaf node in the layout that is equivalent
 * to a cell in a grid. A FieldNode consists of an ObjectField
 * and relevant calculated data to present the field
 * using {@link ToolUiLayoutElement}.
 */
public class FieldNode extends LayoutNode {

    private ObjectField field;

    private ObjectField getField() {
        return field;
    }

    public void setField(ObjectField field) {
        this.field = field;
    }

    public ObjectField getFieldWithToolUiLayoutElement() {
        ObjectField field = this.getField();
        ToolUiLayoutElement layoutElement = new ToolUiLayoutElement();
        layoutElement.setTop(layoutTopOffset);
        layoutElement.setLeft(layoutLeftOffset);
        layoutElement.setWidth(layoutWidth);
        layoutElement.setHeight(1);
        field.as(ToolUi.class).setLayoutField(layoutElement);
        return field;
    }
}
