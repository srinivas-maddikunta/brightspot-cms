package com.psddev.cms.db;

/**
 * Processes String data produced by the rich text editor.
 */
public interface RichTextProcessor {

    /**
     * Performs a transformation on a String that was annotated with
     * {@link com.psddev.cms.db.ToolUi.RichText} and returns the result.
     *
     * @param richText the rich text to process.
     * @return the rich text after processing.
     */
    String process(String richText);
}
