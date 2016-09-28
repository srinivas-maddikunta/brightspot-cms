package com.psddev.cms.rte;

import java.util.Collections;
import java.util.List;

class RichTextViewBuilderStringNode implements RichTextViewBuilderNode {

    private String html;

    RichTextViewBuilderStringNode(String html) {
        this.html = html;
    }

    String getHtml() {
        return html;
    }

    @Override
    public List<Object> toViews(RichTextViewBuilder builder) {
        Object view;
        if (builder.htmlViewFunction != null) {
            view = builder.htmlViewFunction.apply(html);
        } else {
            view = html;
        }
        return view != null ? Collections.singletonList(view) : Collections.emptyList();
    }
}
