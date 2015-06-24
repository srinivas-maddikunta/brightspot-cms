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

        private ContainerType containerType;
        private List<LayoutNode> childNodes;

        public List<LayoutNode> getChildNodes() {
            return childNodes;
        }

        public void setChildNodes(List<LayoutNode> childNodes) {
            this.childNodes = childNodes;
        }

        public ContainerType getContainerType() {
            return containerType;
        }

        public void setContainerType(ContainerType containerType) {
            this.containerType = containerType;
        }

        enum ContainerType {
            ROW, COLUMN
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

    /**
     * Given a root node, will visit all descendant nodes
     * and assign relative widths and offsets to each FieldNode
     *
     * @param rootNode is the node from which to evaluate descendant nodes
     */
    public static void setAllLayoutAttributesFromRoot(LayoutNode rootNode) {
        setAllLayoutAttributes(rootNode, 1, 0, 0);

        //TODO: euclidean to calculate lcm for realWidth of all elements
    }

    /**
     * Recursively sets layout fields
     * @param node for visiting all descendant nodes
     * @param relativeWidth
     * @param layoutTopOffset
     * @param layoutLeftOffset
     */
    private static void setAllLayoutAttributes(LayoutNode node, double relativeWidth, int layoutTopOffset, int layoutLeftOffset) {
        // sets relative width for both node types
        node.setRelativeWidth(relativeWidth);
        node.layoutTopOffset = layoutTopOffset;
        node.layoutLeftOffset = layoutLeftOffset;

        // recursively sets calculated layout fields
        if (node instanceof ContainerNode) {
            ContainerNode containerNode = (ContainerNode) node;
            List<LayoutNode> childNodes = containerNode.getChildNodes();
            int widthsProduct = 0;
            for (LayoutNode childNode : childNodes) {
                widthsProduct = widthsProduct == 0 ? childNode.getWidth() : widthsProduct * childNode.getWidth();
            }

            for (LayoutNode childNode : childNodes) {
                setAllLayoutAttributes(childNode,
                        relativeWidth / widthsProduct * childNode.getWidth(),
                        layoutTopOffset,
                        layoutLeftOffset);
            }
        }
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
