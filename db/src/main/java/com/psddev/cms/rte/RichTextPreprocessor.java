package com.psddev.cms.rte;

import org.jsoup.nodes.Element;

/**
 * Preprocesses a Jsoup document produced by the rich text editor.
 */
public interface RichTextPreprocessor {

    /**
     * Performs a transformation on a rich text Jsoup Document.
     *
     * @param body the body of the rich text document to preprocess.
     */
    void preprocess(Element body);
}
