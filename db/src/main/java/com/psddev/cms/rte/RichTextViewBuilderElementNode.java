package com.psddev.cms.rte;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import com.psddev.dari.util.HtmlElement;

class RichTextViewBuilderElementNode<V> implements RichTextViewBuilderNode<V> {

    private Element element;

    private List<RichTextViewBuilderNode<V>> children = new ArrayList<>();
    private BiFunction<HtmlElement, List<V>, V> htmlElementWrapperViewFunction;

    RichTextViewBuilderElementNode(Element element,
                                   List<RichTextViewBuilderNode<V>> children,
                                   BiFunction<HtmlElement, List<V>, V> htmlElementWrapperViewFunction) {
        this.element = element;
        this.children = children;
        this.htmlElementWrapperViewFunction = htmlElementWrapperViewFunction;
    }

    @Override
    public V toView() {
        if (htmlElementWrapperViewFunction != null) {

            HtmlElement htmlElement = new HtmlElement();
            htmlElement.setName(element.tagName());
            htmlElement.setAttributes(element.attributes().asList()
                    .stream()
                    .collect(Collectors.toMap(Attribute::getKey, Attribute::getValue)));

            return htmlElementWrapperViewFunction.apply(htmlElement,
                    children.stream()
                            .map(RichTextViewBuilderNode::toView)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()));

        } else {
            return null;
        }
    }
}
