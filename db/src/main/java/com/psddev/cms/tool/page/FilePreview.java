package com.psddev.cms.tool.page;

import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.State;
import com.psddev.dari.util.BrightcoveStorageItem;
import com.psddev.dari.util.RoutingFilter;
import com.psddev.dari.util.StorageItem;
import com.psddev.dari.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@RoutingFilter.Path(application = "cms", value = "filePreview")
public class FilePreview extends PageServlet {

    @Override
    protected String getPermissionId() {
        return null;
    }

    public static void reallyDoService(ToolPageContext page) throws IOException, ServletException {
        HttpServletRequest request = page.getRequest();
        State state = State.getInstance(request.getAttribute("object"));
        UUID id = state.getId();

        ObjectField field = (ObjectField) request.getAttribute("field");
        String fieldName = field.getInternalName();
        StorageItem fieldValue = (StorageItem) state.getValue(fieldName);
        String inputName = (String) request.getAttribute("inputName");
        String storageName = inputName + ".storage";
        String pathName = inputName + ".path";
        String contentTypeName = inputName + ".contentType";

        if (fieldValue != null) {
            String contentType = fieldValue.getContentType();

            page.writeStart("div",
                    "class", FileSelector.FILE_SELECTOR_EXISTING_CLASS + " " + FileSelector.FILE_SELECTOR_ITEM_CLASS + " filePreview");
                page.writeTag("input",
                        "name", page.h(storageName),
                        "type", "hidden",
                        "value", page.h(fieldValue.getStorage()));
                page.writeTag("input",
                        "name", page.h(pathName),
                        "type", "hidden",
                        "value", page.h(fieldValue.getPath()));
                page.writeTag("input",
                        "name", page.h(contentTypeName),
                        "type", "hidden",
                        "value", page.h(fieldValue.getPath()));

                if (field.as(ToolUi.class).getStoragePreviewProcessorPath() != null) {
                    ToolUi ui = field.as(ToolUi.class);
                    String processorPath = ui.getStoragePreviewProcessorPath();
                    if (processorPath != null) {
                        page.include(RoutingFilter.Static.getApplicationPath(ui.getStoragePreviewProcessorApplication()) +
                                StringUtils.ensureStart(processorPath, "/"));
                    }
                } else if (contentType != null && contentType.startsWith("image/")) {
                    ImagePreview.reallyDoService(page);
                } else if (fieldValue instanceof BrightcoveStorageItem) {
                    BrightcovePreview.reallyDoService(page);
                } else if (contentType != null && contentType.startsWith("video/")) {
                    page.writeStart("div", "style", page.cssString("margin-bottom", "5px"));
                        page.writeStart("a",
                                "class", "icon icon-action-preview",
                                "href", fieldValue.getPublicUrl(),
                                "target", "_blank");
                            page.writeHtml("View Original");
                        page.writeEnd();
                    page.writeEnd();

                    page.writeStart("video",
                            "controls", "controls",
                            "preload", "auto");
                        page.writeElement("source",
                                "type", contentType,
                                "src", fieldValue.getPublicUrl());
                    page.writeEnd();
                } else {
                    page.writeStart("a",
                            "href", page.h(fieldValue.getPublicUrl()),
                            "target", "_blank");
                        page.writeHtml(page.h(contentType) + ":" + page.h(fieldValue.getPath()));
                    page.writeEnd();
                }
            page.writeEnd();
        }
    }

    @Override
    protected void doService(ToolPageContext page) throws IOException, ServletException {
        reallyDoService(page);
    }
}
