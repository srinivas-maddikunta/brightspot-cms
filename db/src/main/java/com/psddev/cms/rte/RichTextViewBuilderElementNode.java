package com.psddev.cms.rte;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import com.psddev.dari.util.HtmlElement;

class RichTextViewBuilderElementNode implements RichTextViewBuilderNode {

    private Element element;

    private List<RichTextViewBuilderNode> children = new ArrayList<>();

    RichTextViewBuilderElementNode(Element element, List<RichTextViewBuilderNode> children) {
        this.element = element;
        this.children = children;
    }

    @Override
    public List<Object> toViews(RichTextViewBuilder builder) {
        if (builder.htmlElementWrapperViewFunction != null) {

            HtmlElement htmlElement = new HtmlElement();
            htmlElement.setName(element.tagName());
            htmlElement.setAttributes(element.attributes().asList()
                    .stream()
                    .collect(Collectors.toMap(Attribute::getKey, Attribute::getValue)));

            Object view = builder.htmlElementWrapperViewFunction.apply(htmlElement,
                    children.stream()
                            .map(node -> node.toViews(builder))
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList()));

            return view != null ? Collections.singletonList(view) : Collections.emptyList();

        } else {
            return Collections.emptyList();
        }
    }
}
