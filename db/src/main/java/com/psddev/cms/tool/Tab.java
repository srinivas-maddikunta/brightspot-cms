package com.psddev.cms.tool;

import java.io.IOException;

/**
 * Interface for displaying a custom Tab through {@link ToolPageContext)}
 */
public interface Tab {
    /**
     * @return Never {@code null}.
     */
    String getDisplayName();

    /**
     * @param content Can't be {@code null}
     * @return Never {@code null}.
     */
    boolean shouldDisplay(Object content);

    /**
     * @param page Can't be {@code null}
     * @param content Can't be {@code null}
     * @throws IOException if unable to write to the given {@code page}.
     */
    void writeHtml(ToolPageContext page, Object content) throws IOException;
}
