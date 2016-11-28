package com.psddev.cms.rte;

import java.util.function.Function;

import com.psddev.cms.view.RawView;

class RichTextViewBuilderStringNode<V> implements RichTextViewBuilderNode<V> {

    private String html;
    private Function<String, V> htmlViewFunction;

    RichTextViewBuilderStringNode(String html, Function<String, V> htmlViewFunction) {
        this.html = html;
        this.htmlViewFunction = htmlViewFunction;
    }

    String getHtml() {
        return html;
    }

    @Override
    public V toView() {

        if (htmlViewFunction != null) {
            return htmlViewFunction.apply(html);

        } else {
            /*
             * Deliberately cast to an incompatible type if the HTML view
             * function is null. See RichTextViewBuilder for more details.
             */
            @SuppressWarnings("unchecked")
            V rawView = (V) RawView.of(html);

            return rawView;
        }
    }
}
