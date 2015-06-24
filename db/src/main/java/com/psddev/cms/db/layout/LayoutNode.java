package com.psddev.cms.db.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.psddev.cms.db.ToolUi;
import com.psddev.cms.db.ToolUiLayoutElement;
import com.psddev.dari.db.ObjectField;

/**
 * LayoutNode is a tree implementation to represent
 * a layout structure visually represented by nested
 * rows and columns.
 */
abstract class LayoutNode {

    // Values from Definitions
    private int width;
    private LayoutNode parent;

    // Calculated values
    protected transient Collection<LayoutNode> siblings;
    protected transient double relativeWidth;
    protected transient int depth;
    protected transient int layoutWidth;
    protected transient int layoutTopOffset;
    protected transient int layoutLeftOffset;

    public double getRelativeWidth() {
        return relativeWidth;
    }

    public void setRelativeWidth(double relativeWidth) {
        this.relativeWidth = relativeWidth;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public LayoutNode getParent() {
        return parent;
    }

    public void setParent(LayoutNode parent) {
        this.parent = parent;
    }

    /**
     * A ContainerNode is a node that contains multiple
     * child nodes.
     */
    public static class ContainerNode extends LayoutNode {

        private List<LayoutNode> childNodes;

        public List<LayoutNode> getChildNodes() {
            return childNodes;
        }

        public void setChildNodes(List<LayoutNode> childNodes) {
            this.childNodes = childNodes;
        }
    }

    /**
     * FieldNode is a leaf node in the layout that is equivalent
     * to a cell in a grid. A FieldNode consists of an ObjectField
     * and relevant calculated data to present the field
     * using ToolUiElements.
     */
    public static class FieldNode extends LayoutNode {

        private ObjectField field;

        private ObjectField getField() {
            return field;
        }

        public void setField(ObjectField field) {
            this.field = field;
        }

        public ObjectField getFieldWithToolUiLayoutElement() {
            ObjectField field = this.getField();
            field.as(ToolUi.class).setLayoutField(getToolUiLayoutElement());
            return field;
        }

        private ToolUiLayoutElement getToolUiLayoutElement() {
            ToolUiLayoutElement layoutElement = new ToolUiLayoutElement();
            layoutElement.setTop(layoutTopOffset);
            layoutElement.setLeft(layoutLeftOffset);
            layoutElement.setWidth(layoutWidth);
            layoutElement.setHeight(1);

            return layoutElement;
        }
    }

    public static void setAllLayoutAttributesFromRoot(LayoutNode rootNode) {
        setAllLayoutAttributes(rootNode, 1);
    }

    private static void setAllLayoutAttributes(LayoutNode node, double nodeWidth) {
        node.setRelativeWidth(nodeWidth);

        //TOOD: calculate layout fields

    }

    public static List<FieldNode> getAllFieldNodes(LayoutNode rootNode) {
        List<FieldNode> fieldNodes = new ArrayList<>();

        Queue<LayoutNode> nodeQueue = new LinkedList<>();
        nodeQueue.add(rootNode);
        while (!nodeQueue.isEmpty()) {
            LayoutNode currentNode = nodeQueue.remove();

            if (currentNode == null) {
                continue;
            }

            if (currentNode instanceof ContainerNode) {
                ((ContainerNode) currentNode).getChildNodes().stream().forEach(childNode -> nodeQueue.add(childNode));
            } else {
                fieldNodes.add((FieldNode) currentNode);
            }
        }

        return fieldNodes;
    }
}
