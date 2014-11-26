package com.psddev.cms.tool.page;

import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.State;
import com.psddev.dari.util.BrightcoveStorageItem;
import com.psddev.dari.util.RoutingFilter;
import com.psddev.dari.util.StorageItem;
import com.psddev.dari.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@RoutingFilter.Path(application = "cms", value = "filePreview")
public class FilePreview extends PageServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToolPageContext.class);

    @Override
    protected String getPermissionId() {
        return null;
    }

    public static void reallyDoService(ToolPageContext page) throws IOException, ServletException {
        HttpServletRequest request = page.getRequest();
        State state = State.getInstance(request.getAttribute("object"));
        ObjectField field = (ObjectField)request.getAttribute("field");

        if (state == null) {

            UUID typeId = page.param(UUID.class, "typeId");

            if (typeId == null) {
                throw new ServletException("typeId param is empty");
            }

            ObjectType type = ObjectType.getInstance(typeId);
            if(type == null) {
                throw new ServletException("no ObjectType found with typeId: " + typeId);
            }

            state = State.getInstance(type.createObject(null));
        }
        String fieldName = field != null ? field.getInternalName() : page.paramOrDefault(String.class, "fieldName", "");

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

                if (field != null && field.as(ToolUi.class).getStoragePreviewProcessorPath() != null) {
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
