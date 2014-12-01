package com.psddev.cms.tool.page;

import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.State;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.RoutingFilter;
import com.psddev.dari.util.Settings;
import com.psddev.dari.util.StorageItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RoutingFilter.Path(application = "cms", value = "fileSelector")
public class FileSelector extends PageServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToolPageContext.class);
    public static final String FILE_SELECTOR_ITEM_CLASS = "fileSelectorItem";
    public static final String FILE_SELECTOR_EXISTING_CLASS = "fileSelectorExisting";
    public static final String FILE_SELECTOR_NEW_URL_CLASS = "fileSelectorNewUrl";
    public static final String FILE_SELECTOR_NEW_UPLOAD_CLASS = "fileSelectorNewUpload";
    public static final String FILE_SELECTOR_DROPBOX_CLASS = "fileSelectorDropbox";

    @Override
    protected String getPermissionId() {
        return null;
    }

    @Override
    protected void doService(ToolPageContext page) throws IOException, ServletException {
        reallyDoService(page);
    }

    public static void reallyDoService(ToolPageContext page) throws IOException, ServletException {
        HttpServletRequest request = page.getRequest();
        Object object = request.getAttribute("object");

        if (object == null) {
            throw new IOException("object attribute from request is null");
        }

        State state = State.getInstance(object);

        ObjectField field = (ObjectField) request.getAttribute("field");
        String fieldName = field.getInternalName();
        StorageItem fieldValue = (StorageItem) state.getValue(fieldName);

        String inputName = ObjectUtils.firstNonBlank(page.param(String.class, "inputName"),  (String) request.getAttribute("inputName"));
        String actionName = inputName + ".action";
        String fileName = inputName + ".file";
        String urlName = inputName + ".url";
        String dropboxName = inputName + ".dropbox";
        String storageSetting = field.as(ToolUi.class).getStorageSetting() != null ? Settings.getOrDefault(String.class, field.as(ToolUi.class).getStorageSetting(), null) : null;

        page.writeStart("div", "class", "fileSelector", "data-field-name", fieldName, "data-type-id", state.getTypeId(), "data-input-name", inputName, "data-storage", storageSetting, "data-new-path-start", FilePreview.createStorageItemPath(null));

            page.writeStart("select",
                    "id", page.getId(),
                    "class", "toggleable",
                    "data-root", ".inputSmall",
                    "name", actionName);

                if (fieldValue != null) {
                    page.writeStart("option",
                            "data-hide", "." + FILE_SELECTOR_ITEM_CLASS,
                            "data-show", "." + FILE_SELECTOR_EXISTING_CLASS,
                            "value", "keep");
                        page.write("Keep Existing");
                    page.writeEnd();
                }

                page.writeStart("option",
                        "data-hide", "." + FILE_SELECTOR_ITEM_CLASS,
                        "value", "none");
                    page.write("None");
                page.writeEnd();
                page.writeStart("option",
                        "data-hide", "." + FILE_SELECTOR_ITEM_CLASS,
                        "data-show", "." + FILE_SELECTOR_NEW_UPLOAD_CLASS,
                        "value", "newUpload",
                        fieldValue == null && field.isRequired() ? "selected" : "", "");
                    page.write("New Upload");
                page.writeEnd();
                page.writeStart("option",
                        "data-hide", "." + FILE_SELECTOR_ITEM_CLASS,
                        "data-show", "." + FILE_SELECTOR_NEW_URL_CLASS,
                        "value", "newUrl");
                    page.write("New URL");
                page.writeEnd();

                if (!ObjectUtils.isBlank(page.getCmsTool().getDropboxApplicationKey())) {
                    page.writeStart("option",
                            "data-hide", "." + FILE_SELECTOR_ITEM_CLASS,
                            "data-show", "." + FILE_SELECTOR_DROPBOX_CLASS,
                            "value", "dropbox");
                        page.write("Dropbox");
                    page.writeEnd();
                }

            page.writeEnd();

            page.writeTag("input",
                    "class", FILE_SELECTOR_ITEM_CLASS + " " + FILE_SELECTOR_NEW_UPLOAD_CLASS,
                    "type", "file",
                    "name", page.h(fileName));
            page.writeTag("input",
                    "class", FILE_SELECTOR_ITEM_CLASS + " " + FILE_SELECTOR_NEW_URL_CLASS,
                    "type", "text",
                    "name", page.h(urlName));

            if (!ObjectUtils.isBlank(page.getCmsTool().getDropboxApplicationKey())) {
                page.writeStart("span",
                        "class", FILE_SELECTOR_ITEM_CLASS + " " + FILE_SELECTOR_DROPBOX_CLASS,
                        "style", "display: inline-block; vertical-align: bottom");
                    page.writeTag("input",
                            "type", "dropbox-chooser",
                            "name", page.h(dropboxName),
                            "data-link-type", "direct",
                            "style", "visibility:hidden");
                page.writeEnd();
                page.writeStart("script", "type", "text/javascript");
                    page.writeRaw(
                            "$('.fileSelectorDropbox input').on('DbxChooserSuccess', function(event) {\n" +
                            "   $(this).val(JSON.stringify(event.originalEvent.files[0]));\n" +
                            "});");
                page.writeEnd();
            }

        page.writeEnd();

        if (fieldValue != null) {
            FilePreview.reallyDoService(page);
        }
    }
}
