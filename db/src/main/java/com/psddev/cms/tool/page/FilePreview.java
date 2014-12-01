package com.psddev.cms.tool.page;

import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.State;
import com.psddev.dari.util.AggregateException;
import com.psddev.dari.util.BrightcoveStorageItem;
import com.psddev.dari.util.ImageMetadataMap;
import com.psddev.dari.util.IoUtils;
import com.psddev.dari.util.MultipartRequest;
import com.psddev.dari.util.MultipartRequestFilter;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.RoutingFilter;
import com.psddev.dari.util.Settings;
import com.psddev.dari.util.SparseSet;
import com.psddev.dari.util.StorageItem;
import com.psddev.dari.util.StringUtils;
import com.psddev.dari.util.TypeReference;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
        String inputName = ObjectUtils.firstNonBlank(page.param(String.class, "inputName"), (String) request.getAttribute("inputName"));
        String storageName = inputName + ".storage";
        String pathName = inputName + ".path";
        String contentTypeName = inputName + ".contentType";

        String fieldName = field != null ? field.getInternalName() : page.paramOrDefault(String.class, "fieldName", "");
        StorageItem fieldValue = null;

        //TODO: move somewhere reusable?
        //handle inline upload display
        if (page.paramOrDefault(Boolean.class, "isNewUpload", false)) {
            String storageItemPath = page.param(String.class, pathName);
            if (!StringUtils.isBlank(storageItemPath)) {
                StorageItem newItem = StorageItem.Static.createIn(page.param(storageName));
                newItem.setPath(page.param(pathName));
                //newItem.setContentType(page.param(contentTypeName));
                fieldValue = newItem;
            }

            state = State.getInstance(ObjectType.getInstance(page.param(UUID.class, "typeId")));
        }

        if (fieldValue == null) {
            fieldValue = (StorageItem) state.getValue(fieldName);
        }


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

    private void doFormPost(ToolPageContext page) throws IOException, ServletException {
        HttpServletRequest request = page.getRequest();
        Object object = request.getAttribute("object");

        if (object == null) {
            return;
        }

        State state = State.getInstance(object);

        ObjectField field = (ObjectField) request.getAttribute("field");
        String fieldName = field.getInternalName();
        StorageItem fieldValue = (StorageItem) state.getValue(fieldName);

        String inputName = (String) page.getRequest().getAttribute("inputName");
        String actionName = inputName + ".action";
        String storageName = inputName + ".storage";
        String fileName = inputName + ".file";
        String urlName = inputName + ".url";
        String pathName = inputName + ".path";
        String dropboxName = inputName + ".dropbox";
        String contentTypeName = inputName + ".contentType";

        String metadataFieldName = fieldName + ".metadata";

        Map<String, Object> fieldValueMetadata = null;
        if (fieldValue != null) {
            fieldValueMetadata = fieldValue.getMetadata();
        }

        if (fieldValueMetadata == null) {
            fieldValueMetadata = new LinkedHashMap<String, Object>();
        }

        File file = null;

        try {
            String action = page.param(actionName);
            StorageItem newItem = null;

            InputStream newItemData = null;

            if ("keep".equals(action)) {
                if (fieldValue != null) {
                    newItem = fieldValue;
                } else {
                    newItem = StorageItem.Static.createIn(page.param(storageName));
                    newItem.setPath(page.param(pathName));
                    newItem.setContentType(page.param(contentTypeName));
                }

            } else if ("newUpload".equals(action) ||
                    "dropbox".equals(action)) {
                String name = null;
                String fileContentType = null;
                long fileSize = 0;
                file = File.createTempFile("cms.", ".tmp");
                MultipartRequest mpRequest;

                if ("dropbox".equals(action)) {
                    Map<String, Object> fileData = (Map<String, Object>) ObjectUtils.fromJson(page.param(String.class, dropboxName));

                    if (fileData != null) {
                        name = ObjectUtils.to(String.class, fileData.get("name"));
                        fileContentType = ObjectUtils.getContentType(name);
                        fileSize = ObjectUtils.to(long.class, fileData.get("bytes"));
                        InputStream fileInput = new URL(ObjectUtils.to(String.class, fileData.get("link"))).openStream();

                        try {
                            FileOutputStream fileOutput = new FileOutputStream(file);

                            try {
                                IoUtils.copy(fileInput, fileOutput);

                            } finally {
                                fileOutput.close();
                            }

                        } finally {
                            fileInput.close();
                        }
                    }

                } else if ((mpRequest = MultipartRequestFilter.Static.getInstance(request)) != null) {
                    FileItem fileItem = mpRequest.getFileItem(fileName);

                    if (fileItem != null) {
                        name = fileItem.getName();
                        fileContentType = fileItem.getContentType();
                        fileSize = fileItem.getSize();

                        try {
                            fileItem.write(file);
                        } catch (Exception e) {
                            //ignore
                        }
                    }
                }

                if (name != null &&
                        fileContentType != null) {

                    // Checks to make sure the file's content type is valid
                    String groupsPattern = Settings.get(String.class, "cms/tool/fileContentTypeGroups");
                    Set<String> contentTypeGroups = new SparseSet(ObjectUtils.isBlank(groupsPattern) ? "+/" : groupsPattern);
                    if (!contentTypeGroups.contains(fileContentType)) {
                        state.addError(field, String.format(
                                "Invalid content type [%s]. Must match the pattern [%s].",
                                fileContentType, contentTypeGroups));
                        return;
                    }

                    // Disallow HTML disguising as other content types per:
                    // http://www.adambarth.com/papers/2009/barth-caballero-song.pdf
                    if (!contentTypeGroups.contains("text/html")) {
                        InputStream input = new FileInputStream(file);

                        try {
                            byte[] buffer = new byte[1024];
                            String data = new String(buffer, 0, input.read(buffer)).toLowerCase(Locale.ENGLISH);
                            String ptr = data.trim();

                            if (ptr.startsWith("<!") ||
                                    ptr.startsWith("<?") ||
                                    data.startsWith("<html") ||
                                    data.startsWith("<script") ||
                                    data.startsWith("<title") ||
                                    data.startsWith("<body") ||
                                    data.startsWith("<head") ||
                                    data.startsWith("<plaintext") ||
                                    data.startsWith("<table") ||
                                    data.startsWith("<img") ||
                                    data.startsWith("<pre") ||
                                    data.startsWith("text/html") ||
                                    data.startsWith("<a") ||
                                    ptr.startsWith("<frameset") ||
                                    ptr.startsWith("<iframe") ||
                                    ptr.startsWith("<link") ||
                                    ptr.startsWith("<base") ||
                                    ptr.startsWith("<style") ||
                                    ptr.startsWith("<div") ||
                                    ptr.startsWith("<p") ||
                                    ptr.startsWith("<font") ||
                                    ptr.startsWith("<applet") ||
                                    ptr.startsWith("<meta") ||
                                    ptr.startsWith("<center") ||
                                    ptr.startsWith("<form") ||
                                    ptr.startsWith("<isindex") ||
                                    ptr.startsWith("<h1") ||
                                    ptr.startsWith("<h2") ||
                                    ptr.startsWith("<h3") ||
                                    ptr.startsWith("<h4") ||
                                    ptr.startsWith("<h5") ||
                                    ptr.startsWith("<h6") ||
                                    ptr.startsWith("<b") ||
                                    ptr.startsWith("<br")) {
                                state.addError(field, String.format(
                                        "Can't upload [%s] file disguising as HTML!",
                                        fileContentType));
                                return;
                            }

                        } finally {
                            input.close();
                        }
                    }

                    if (fileSize > 0) {
                        String idString = UUID.randomUUID().toString().replace("-", "");
                        StringBuilder pathBuilder = new StringBuilder();
                        String label = state.getLabel();

                        fieldValueMetadata.put("originalFilename", name);

                        int lastDotAt = name.indexOf('.');
                        String extension;

                        if (lastDotAt > -1) {
                            extension = name.substring(lastDotAt);
                            name = name.substring(0, lastDotAt);

                        } else {
                            extension = "";
                        }

                        if (ObjectUtils.isBlank(label) ||
                                ObjectUtils.to(UUID.class, label) != null) {
                            label = name;
                        }

                        if (ObjectUtils.isBlank(label)) {
                            label = UUID.randomUUID().toString().replace("-", "");
                        }

                        pathBuilder.append(idString.substring(0, 2));
                        pathBuilder.append('/');
                        pathBuilder.append(idString.substring(2, 4));
                        pathBuilder.append('/');
                        pathBuilder.append(idString.substring(4));
                        pathBuilder.append('/');
                        pathBuilder.append(StringUtils.toNormalized(label));
                        pathBuilder.append(extension);

                        String storageSetting = field.as(ToolUi.class).getStorageSetting();

                        newItem = StorageItem.Static.createIn(storageSetting != null ? Settings.getOrDefault(String.class, storageSetting, null) : null);
                        newItem.setPath(pathBuilder.toString());
                        newItem.setContentType(fileContentType);

                        Map<String, List<String>> httpHeaders = new LinkedHashMap<String, List<String>>();
                        httpHeaders.put("Cache-Control", Collections.singletonList("public, max-age=31536000"));
                        httpHeaders.put("Content-Length", Collections.singletonList(String.valueOf(fileSize)));
                        httpHeaders.put("Content-Type", Collections.singletonList(fileContentType));
                        fieldValueMetadata.put("http.headers", httpHeaders);

                        newItem.setData(new FileInputStream(file));

                        newItemData = new FileInputStream(file);
                    }
                }

            } else if ("newUrl".equals(action)) {
                newItem = StorageItem.Static.createUrl(page.param(urlName));

                newItemData = newItem.getData();
            }

            // Automatic image metadata extraction.
            if (newItem != null && !"keep".equals(action)) {
                if (newItemData == null) {
                    newItemData = newItem.getData();
                }

                String contentType = newItem.getContentType();

                if (contentType != null && contentType.startsWith("image/")) {
                    try {
                        ImageMetadataMap metadata = new ImageMetadataMap(newItemData);
                        fieldValueMetadata.putAll(metadata);

                        List<Throwable> errors = metadata.getErrors();
                        if (!errors.isEmpty()) {
                            LOGGER.debug("Can't read image metadata!", new AggregateException(errors));
                        }

                    } finally {
                        IoUtils.closeQuietly(newItemData);
                    }
                }
            }

            // Transfers legacy metadata over to it's new location within the StorageItem object
            Map<String, Object> legacyMetadata = ObjectUtils.to(new TypeReference<Map<String, Object>>() { }, state.getValue(metadataFieldName));
            if (legacyMetadata != null && !legacyMetadata.isEmpty()) {
                for (Map.Entry<String, Object> entry : legacyMetadata.entrySet()) {
                    if (!fieldValueMetadata.containsKey(entry.getKey())) {
                        fieldValueMetadata.put(entry.getKey(), entry.getValue());
                    }
                }
                state.remove(metadataFieldName);
            }

            if (newItem != null) {
                newItem.setMetadata(fieldValueMetadata);
            }

            if (newItem != null &&
                    ("newUpload".equals(action) ||
                            "dropbox".equals(action))) {
                newItem.save();
            }

            state.putValue(fieldName, newItem);

        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }

    //TODO: where should this go?
    public static String createStorageItemPath(String fileName) {
        String idString = UUID.randomUUID().toString().replace("-", "");
        StringBuilder pathBuilder = new StringBuilder();
        //String label = state.getLabel();
        String label = null;
        String extension = "";

        if (!ObjectUtils.isBlank(fileName)) {
            int lastDotAt = fileName.indexOf('.');

            if (lastDotAt > -1) {
                extension = fileName.substring(lastDotAt);
                fileName = fileName.substring(0, lastDotAt);

            }
        }

        if (ObjectUtils.isBlank(label) ||
                ObjectUtils.to(UUID.class, label) != null) {
            label = fileName;
        }

        if (ObjectUtils.isBlank(label)) {
            label = UUID.randomUUID().toString().replace("-", "");
        }

        pathBuilder.append(idString.substring(0, 2));
        pathBuilder.append('/');
        pathBuilder.append(idString.substring(2, 4));
        pathBuilder.append('/');
        pathBuilder.append(idString.substring(4));
        pathBuilder.append('/');
        pathBuilder.append(StringUtils.toNormalized(label));
        pathBuilder.append(extension);

        return pathBuilder.toString();
    }

    @Override
    protected void doService(ToolPageContext page) throws IOException, ServletException {
        reallyDoService(page);
    }
}
