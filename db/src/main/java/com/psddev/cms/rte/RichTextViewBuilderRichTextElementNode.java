package com.psddev.cms.rte;

import java.util.Collections;
import java.util.List;

import com.psddev.cms.db.RichTextElement;

class RichTextViewBuilderRichTextElementNode implements RichTextViewBuilderNode{

    private RichTextElement richTextElement;

    RichTextViewBuilderRichTextElementNode(RichTextElement richTextElement) {
        this.richTextElement = richTextElement;
    }

    @Override
    public List<Object> toViews(RichTextViewBuilder builder) {
        Object view = null;
        if (builder.richTextElementViewFunction != null) {
            view = builder.richTextElementViewFunction.apply(richTextElement);
        }
        return view != null ? Collections.singletonList(view) : Collections.emptyList();
    }
}
