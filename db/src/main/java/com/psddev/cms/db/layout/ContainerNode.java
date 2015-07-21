package com.psddev.cms.db.layout;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerNode extends LayoutNode {

    private List<LayoutNode> childNodes;

    public List<LayoutNode> getChildNodes() {
        if (childNodes == null) {
            childNodes = new ArrayList<>();
        }
        return childNodes;
    }

    public void setChildNodes(List<LayoutNode> childNodes) {
        this.childNodes = childNodes;
    }
}
