package com.psddev.cms.rte;

import java.util.function.Function;

class RichTextViewBuilderStringNode<T> implements RichTextViewBuilderNode<T> {

    private String html;
    private Function<String, T> htmlViewFunction;

    RichTextViewBuilderStringNode(String html, Function<String, T> htmlViewFunction) {
        this.html = html;
        this.htmlViewFunction = htmlViewFunction;
    }

    String getHtml() {
        return html;
    }

    @Override
    public T toView() {
        return htmlViewFunction != null ? htmlViewFunction.apply(html) : null;
    }
}
