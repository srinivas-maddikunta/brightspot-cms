package com.psddev.cms.tool.page.content;

import com.psddev.cms.db.Overlay;
import com.psddev.cms.db.OverlayProvider;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.State;

import java.io.IOException;
import java.util.List;

public class Edit {

    public static Overlay getOverlay(Object content) {
        return content != null
                ? (Overlay) State.getInstance(content).getExtras().get("cms.tool.overlay")
                : null;
    }

    public static void writeOverlayProviderSelect(ToolPageContext page, OverlayProvider selected) throws IOException {
        List<OverlayProvider> overlayProviders = Query.from(OverlayProvider.class).selectAll();

        if (overlayProviders.isEmpty()) {
            return;
        }

        page.writeStart("ul", "class", "piped");
        {
            page.writeStart("li", "class", selected == null ? "selected" : null);
            {
                page.writeStart("a",
                        "href", page.url("", "overlayId", null));
                page.writeHtml("Default");
                page.writeEnd();
            }
            page.writeEnd();

            for (OverlayProvider overlayProvider : overlayProviders) {
                page.writeStart("li", "class", overlayProvider.equals(selected) ? "selected" : null);
                {
                    page.writeStart("a",
                            "href", page.url("", "overlayId", overlayProvider.getState().getId()));
                    page.writeObjectLabel(overlayProvider);
                    page.writeEnd();
                }
                page.writeEnd();
            }
        }
        page.writeEnd();
    }
}
