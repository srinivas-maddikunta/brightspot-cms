package com.psddev.cms.tool;

import java.io.IOException;
import java.util.Collection;

/**
 * Interface for displaying a custom Tab in
 * {@link ToolPageContext#writeSomeFormFields(Object, boolean, Collection, Collection)}
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
