package com.psddev.cms.tool;

import java.io.IOException;

public interface Tab {
    String getDisplayName();
    boolean shouldDisplay(Object content);
    void writeHtml(ToolPageContext page, Object content) throws IOException;
}
