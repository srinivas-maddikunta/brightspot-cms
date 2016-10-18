package com.psddev.cms.rte;

import org.jsoup.nodes.Element;

/**
 * Processes a Jsoup document produced by the rich text editor.
 */
public interface RichTextProcessor {

    /**
     * Performs a transformation on a rich text Jsoup Document.
     *
     * @param body the body of the rich text document to process.
     */
    void process(Element body);
}
