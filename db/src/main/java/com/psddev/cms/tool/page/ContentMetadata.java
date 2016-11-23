package com.psddev.cms.tool.page;

import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.util.CompactMap;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.RoutingFilter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

/**
 * Displays core image metadata that has been extracted via
 * <a href="http://www.drewnoakes.com/code/exif/">Metadata Extractor</a>.
 */
@RoutingFilter.Path(application = "cms", value = ContentMetadata.PATH)
public class ContentMetadata extends PageServlet {

    public static final String PATH = "/contentMetadata";

    @Override
    protected String getPermissionId() {
        return null;
    }

    @Override
    protected void doService(ToolPageContext page) throws IOException, ServletException {
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) ObjectUtils.fromJson(page.param(String.class, "metadata"));

        // Disable remove button.
        page.writeStart("style", "type", "text/css"); {
            page.writeCss(".removeButton", "display", "none !important");
        }
        page.writeEnd();

        page.writeStart("div", "class", "widget"); {
            page.writeStart("h1").writeHtml(page.localize(ContentMetadata.class, "title")).writeEnd();
            page.writeStart("div", "class", "repeatableForm"); {
                page.writeStart("ul"); {
                    for (Map.Entry<String, Object> entry : metadata.entrySet()) {
                        writeEntry(page, entry);
                    }
                }
                page.writeEnd();
            }
            page.writeEnd();
        }
        page.writeEnd();
    }

    /*
     * Recursively writes metadata.
     */
    @SuppressWarnings("unchecked")
    private void writeEntry(ToolPageContext page, Map.Entry<String, Object> entry) throws IOException {
        String key = entry.getKey();
        Object value = entry.getValue();
        String label = key.substring(0, 1).toUpperCase() + key.substring(1);

        if (value instanceof CompactMap) {
            CompactMap valueMap = (CompactMap) value;

            if (!valueMap.isEmpty()) {
                page.writeStart("li", "data-type", label); {
                    page.writeStart("div", "class", "objectInputs"); {
                        page.writeStart("div", "class", "repeatableForm"); {
                            page.writeStart("ul"); {
                                for (Object obj : valueMap.entrySet()) {
                                    Map.Entry subEntry = (Map.Entry) obj;
                                    writeEntry(page, subEntry);
                                }
                            }
                            page.writeEnd();
                        }
                        page.writeEnd();
                    }
                    page.writeEnd();
                }
                page.writeEnd();
                return;
            }

        } else {
            label += ": " + value;
        }

        page.writeStart("li", "data-type", label).writeEnd();

        // Disable pointer events.
        page.writeStart("style", "type", "text/css"); {
            String labelSelector = "li[data-type=\"" + label + "\"] .repeatableLabel";
            page.writeCss(labelSelector, "pointer-events", "none");
            page.writeCss(labelSelector + ":after", "content", "none !important");
        }
        page.writeEnd();
    }
}
