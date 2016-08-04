package com.psddev.cms.tool;

import java.io.IOException;

/**
 * Executed by {@link ToolPageContext#writeHeader()} to add
 * additional elements to the <head> of {@link Tool} pages.
 */
public interface ToolPageHead {

    /**
     * Use for writing additional elements to
     * the <head> of the CMS.
     *
     * @param page to invoke {@link com.psddev.dari.util.HtmlWriter} methods.
     *
     */
    void writeHtml(ToolPageContext page) throws IOException;
}
