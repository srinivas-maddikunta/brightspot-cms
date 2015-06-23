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

    // Values from Definition
    private int width;
    private LayoutNode parent;

    // Calculated values
    protected transient Collection<LayoutNode> siblings;
    protected transient int realWidth;
    protected transient int depth;

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

        private Collection<LayoutNode> childNodes;

        public Collection<LayoutNode> getChildNodes() {
            return childNodes;
        }

        public void setChildNodes(Collection<LayoutNode> childNodes) {
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

        // Calculated values
        protected transient int topOffset;
        protected transient int leftOffset;

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
            layoutElement.setTop(topOffset);
            layoutElement.setLeft(leftOffset);
            layoutElement.setWidth(realWidth);
            layoutElement.setHeight(1);

            return layoutElement;
        }
    }

    public static LayoutNode findSmallestLayoutNode(LayoutNode node) {

        if (node == null) {
            return null;
        }

        Queue<LayoutNode> nodeQueue = new LinkedList<>();
        nodeQueue.add(node);
        int depth = 0;
        LayoutNode deepestNode = node;
        deepestNode.depth = depth;

        while (!nodeQueue.isEmpty()) {
            LayoutNode currentNode = nodeQueue.remove();

            if (currentNode == null) {
                continue;
            }

            if (currentNode instanceof ContainerNode) {
                ((ContainerNode) currentNode).getChildNodes().stream().forEach(childNode -> nodeQueue.add(childNode));
                depth++;
            } else {
                FieldNode fieldNode = (FieldNode) currentNode;
                fieldNode.depth = depth;

                if (fieldNode.depth > deepestNode.depth ||
                        (fieldNode.depth == deepestNode.depth
                                && fieldNode.getWidth() < deepestNode.getWidth())) {

                    deepestNode = fieldNode;
                }
            }
        }

        return deepestNode;
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
