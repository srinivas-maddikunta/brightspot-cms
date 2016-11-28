package com.psddev.cms.rte;

import java.util.function.Function;

import com.psddev.cms.db.RichTextElement;

class RichTextViewBuilderRichTextElementNode<V> implements RichTextViewBuilderNode<V> {

    private final RichTextElement richTextElement;
    private final Function<RichTextElement, V> richTextElementViewFunction;

    RichTextViewBuilderRichTextElementNode(RichTextElement richTextElement,
                                           Function<RichTextElement, V> richTextElementViewFunction) {
        this.richTextElement = richTextElement;
        this.richTextElementViewFunction = richTextElementViewFunction;
    }

    @Override
    public V toView() {
        return richTextElementViewFunction != null ? richTextElementViewFunction.apply(richTextElement) : null;
    }
}
