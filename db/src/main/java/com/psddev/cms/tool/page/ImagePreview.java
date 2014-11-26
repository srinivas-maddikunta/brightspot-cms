package com.psddev.cms.tool.page;

import com.psddev.cms.db.ImageCrop;
import com.psddev.cms.db.ImageTag;
import com.psddev.cms.db.ImageTextOverlay;
import com.psddev.cms.db.ResizeOption;
import com.psddev.cms.db.StandardImageSize;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.ColorDistribution;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ReferentialText;
import com.psddev.dari.db.State;
import com.psddev.dari.util.AggregateException;
import com.psddev.dari.util.ClassFinder;
import com.psddev.dari.util.CollectionUtils;
import com.psddev.dari.util.ImageEditor;
import com.psddev.dari.util.ImageMetadataMap;
import com.psddev.dari.util.IoUtils;
import com.psddev.dari.util.JavaImageEditor;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

@RoutingFilter.Path(application = "cms", value = "imagePreview")
public class ImagePreview extends PageServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToolPageContext.class);
    public static final String NEW_STORAGE_ITEM_ATTR = "newStorageItem";

    @Override
    protected String getPermissionId() {
        return null;
    }

    public static void reallyDoService(ToolPageContext page) throws IOException, ServletException {

        if (page.isFormPost()) {
            processForm(page);
        }

        String storageItemPath = page.param(String.class, "path");
        if (!StringUtils.isBlank(storageItemPath)) {
            StorageItem storageItem = createStorageItem(storageItemPath);
            if (storageItem != null) {
                page.getRequest().setAttribute(NEW_STORAGE_ITEM_ATTR, storageItem);
            }
        }

        if (page.paramOrDefault(boolean.class, "upload", false)) {
            renderImagePreview(page);
        } else {
            renderImageEditor(page);
        }
    }

    public static StorageItem createStorageItem(String storageItemPath) {
        return null;
    }

    public static void renderImageEditor(ToolPageContext page) throws IOException, ServletException {

        HttpServletRequest request = page.getRequest();
        State state = State.getInstance(request.getAttribute("object"));
        UUID id = state.getId();
        ObjectField field = (ObjectField) request.getAttribute("field");
        String fieldName = field.getInternalName();
        StorageItem fieldValue = (StorageItem) state.getValue(fieldName);

        Class hotspotClass = ObjectUtils.getClassByName(ImageTag.HOTSPOT_CLASS);
        boolean projectUsingBrightSpotImage = hotspotClass != null && !ObjectUtils.isBlank(ClassFinder.Static.findClasses(hotspotClass));

        if (projectUsingBrightSpotImage) {
            page.include("set/hotSpot.jsp");
        }

        page.writeStart("div", "class", "imageEditor");
            writeImageEditorAside(page, fieldValue, state, id, fieldName);
            writeImageEditorImage(page, fieldValue);
        page.writeEnd();

        if (projectUsingBrightSpotImage) {
            page.include("set/hotSpot.jsp");
        }
    }

    public static void renderImagePreview(ToolPageContext page) throws  IOException, ServletException {

        page.writeStart("div", "class", "upload-preview loading");
            page.writeTag("img");
            page.writeStart("div",
                    "class", "radial-progress",
                    "data-progress", "0");
                page.writeStart("div", "class", "circle");
                    page.writeStart("div", "class", "mask full");
                        page.writeStart("div", "class", "fill");
                        page.writeEnd();
                    page.writeEnd();
                    page.writeStart("div", "class", "mask half");
                        page.writeStart("div", "class", "fill");
                        page.writeEnd();
                        page.writeStart("div", "class", "fill fix");
                        page.writeEnd();
                    page.writeEnd();
                page.writeEnd();
                page.writeStart("div", "class", "inset");
                    page.writeStart("div", "class", "percentage");
                        page.writeStart("span");
                        page.writeEnd();
                    page.writeEnd();
                page.writeEnd();
            page.writeEnd();
        page.writeEnd();
    }

    @Override
    protected void doService(ToolPageContext page) throws IOException, ServletException {
        reallyDoService(page);
    }

    private static void writeImageEditorAside(ToolPageContext page, StorageItem fieldValue, State state, UUID id, String fieldName) throws IOException {

        Map<String, Object> fieldValueMetadata = null;
        if (fieldValue != null) {
            fieldValueMetadata = fieldValue.getMetadata();
        }

        if (fieldValueMetadata == null) {
            fieldValueMetadata = new LinkedHashMap<String, Object>();
        }

        page.writeStart("div", "class", "imageEditor-aside");
            writeImageEditorTools(page, fieldValue, state, id, fieldName);
            writeImageEditorEdit(page, fieldValue, state, id, fieldName, fieldValueMetadata);
            writeImageEditorSizes(page, fieldValue, state, fieldName, fieldValueMetadata);
        page.writeEnd();
    }

    private static void writeImageEditorTools(ToolPageContext page, StorageItem fieldValue, State state, UUID id, String fieldName) throws IOException {

        page.writeStart("div", "class", "imageEditor-tools");
            page.writeStart("h2");
                page.write("Tools");
            page.writeEnd();

            page.writeStart("ul");

                if (state.as(ColorDistribution.Data.class).getDistribution() != null) {
                    page.writeStart("li");
                        page.writeStart("a",
                                "class", "icon icon-tint",
                                "href", page.h(page.cmsUrl("/contentColors", "id", id)),
                                "target", "contentColors");
                            page.write("Colors");
                        page.writeEnd();
                    page.writeEnd();
                }

                page.writeStart("li");
                    page.writeStart("a",
                            "class", "action-preview",
                            "href", page.h(fieldValue.getPublicUrl()),
                            "target", "_blank");
                        page.write("View Original");
                    page.writeEnd();
                page.writeEnd();

                page.writeStart("li");
                    page.writeStart("a",
                            "class", "icon icon-crop",
                            "href", page.h(page.url("/contentImage", "id", id, "field", fieldName)),
                            "target", "contentImages");
                        page.write("View Resized");
                    page.writeEnd();
                page.writeEnd();

            page.writeEnd();

        page.writeEnd();
    }

    private static void writeImageEditorEdit(ToolPageContext page, StorageItem fieldValue, State state, UUID id, String fieldName, Map<String, Object> fieldValueMetadata) throws IOException {
        HttpServletRequest request = page.getRequest();
        String inputName = (String) request.getAttribute("inputName");
        boolean useJavaImageEditor = ImageEditor.Static.getDefault() != null && (ImageEditor.Static.getDefault() instanceof JavaImageEditor);
        String blurName = inputName + ".blur";

        Map<String, Object> edits = (Map<String, Object>) fieldValueMetadata.get("cms.edits");

        if (edits == null) {
            edits = new HashMap<String, Object>();
            fieldValueMetadata.put("cms.edits", edits);
        }

        List<String> blurs = new ArrayList<String>();
        if (!ObjectUtils.isBlank(edits.get("blur"))) {
            Object blur = edits.get("blur");
            if (blur instanceof String && ObjectUtils.to(String.class, blur).matches("(\\d+x){3}\\d+")) {
                blurs.add(ObjectUtils.to(String.class, blur));
            } else if (blur instanceof List) {
                for (Object blurItem : (List) blur) {
                    String blurValue = ObjectUtils.to(String.class, blurItem);
                    if (blurValue.matches("(\\d+x){3}\\d+")) {
                        blurs.add(blurValue);
                    }
                }
            }
        }

        page.writeStart("div", "class", "imageEditor-edit");

            page.writeStart("h2");
                page.write("Adjustments");
            page.writeEnd();

            page.writeStart("table");
                page.writeStart("tbody");

                    if (useJavaImageEditor) {
                        page.writeStart("tr");
                            page.writeStart("th");
                                page.write("Blur");
                            page.writeEnd();
                            page.writeStart("td");
                                page.writeStart("a",
                                        "class", "imageEditor-addBlurOverlay");
                                    page.write("Add Blur");
                                page.writeEnd();
                                page.writeTag("br");

                                if (!ObjectUtils.isBlank(blurs)) {
                                    for (String blur : blurs) {
                                        page.writeTag("input",
                                                "type", "hidden",
                                                "name", page.h(blurName),
                                                "value", blur);
                                    }
                                }
                            page.writeEnd();
                        page.writeEnd();
                    }

                    for (ImageAdjustment adj : ImageAdjustment.values()) {
                        page.writeStart("tr");
                            page.writeStart("th");
                                page.writeHtml(page.h(StringUtils.toPascalCase(adj.title)));
                            page.writeEnd();
                            page.writeStart("td");
                            if (!adj.javaImageEditorOnly || useJavaImageEditor) {
                                page.writeTag("input",
                                        "type", adj.inputType,
                                        "name", inputName + "." + adj.title,
                                        adj.inputType.equals("range") ? "min" : "", adj.inputType.equals("range") ? adj.min : "",
                                        adj.inputType.equals("range") ? "max" : "", adj.inputType.equals("range") ? adj.max : "",
                                        adj.inputType.equals("range") ? "step" : "", adj.inputType.equals("range") ? adj.step : "",
                                        "value", ObjectUtils.to(adj.valueType, edits.get(adj.title)));
                            }

                            page.writeEnd();
                        page.writeEnd();
                    }

                page.writeEnd();
            page.writeEnd();

        page.writeEnd();
    }

    private static void writeImageEditorSizes(ToolPageContext page, StorageItem fieldValue, State state, String fieldName, Map<String, Object> fieldValueMetadata) throws IOException {

        String cropsFieldName = fieldName + ".crops";

        Map<String, ImageCrop> crops = ObjectUtils.to(new TypeReference<Map<String, ImageCrop>>() { }, fieldValueMetadata.get("cms.crops"));
        if (crops == null) {
            // for backward compatibility
            crops = ObjectUtils.to(new TypeReference<Map<String, ImageCrop>>() { }, state.getValue(cropsFieldName));
        }
        if (crops == null) {
            crops = new HashMap<String, ImageCrop>();
        }

        crops = new TreeMap<String, ImageCrop>(crops);

        Map<String, StandardImageSize> sizes = new HashMap<String, StandardImageSize>();
        for (StandardImageSize size : StandardImageSize.findAll()) {
            String sizeId = size.getId().toString();
            sizes.put(sizeId, size);
            if (crops.get(sizeId) == null) {
                crops.put(sizeId, new ImageCrop());
            }
        }

        page.writeStart("div", "class", "imageEditor-sizes");
            page.writeStart("h2");
                page.write("Standard Sizes");
            page.writeEnd();

            page.writeStart("table");
                page.writeStart("tbody");
                    for (Map.Entry<String, ImageCrop> e : crops.entrySet()) {
                        String cropId = e.getKey();
                        ImageCrop crop = e.getValue();
                        StandardImageSize size = sizes.get(cropId);
                        if (size == null && ObjectUtils.to(UUID.class, cropId) != null) {
                            continue;
                        }
                        if (size != null) {
                            page.writeStart("tr",
                                    "data-size-name", size.getInternalName(),
                                    "data-size-independent", size.isIndependent(),
                                    "data-size-width", size.getWidth(),
                                    "data-size-height", size.getHeight());
                                page.writeStart("th");
                                    page.write(page.h(size.getDisplayName()));
                                page.writeEnd();
                        } else {
                            page.writeStart("tr");
                                page.writeStart("th");
                                    page.write(page.h(cropId));
                                page.writeEnd();
                        }
                            page.writeStart("td");
                                page.writeTag("input",
                                        "name", page.h(cropsFieldName + "." + cropId + ".x"),
                                        "type", "text",
                                        "value", crop.getX());
                            page.writeEnd();
                            page.writeStart("td");
                                page.writeTag("input",
                                        "name", page.h(cropsFieldName + "." + cropId + ".y"),
                                        "type", "text",
                                        "value", crop.getY());
                            page.writeEnd();
                            page.writeStart("td");
                                page.writeTag("input",
                                        "name", page.h(cropsFieldName + "." + cropId + ".width"),
                                        "type", "text",
                                        "value", crop.getWidth());
                            page.writeEnd();
                            page.writeStart("td");
                                page.writeTag("input",
                                        "name", page.h(cropsFieldName + "." + cropId + ".height"),
                                        "type", "text",
                                        "value", crop.getHeight());
                            page.writeEnd();
                            page.writeStart("td");
                                page.writeTag("input",
                                        "name", page.h(cropsFieldName + "." + cropId + ".texts"),
                                        "type", "text",
                                        "value", crop.getTexts());
                            page.writeEnd();
                            page.writeStart("td");
                                page.writeTag("input",
                                        "name", page.h(cropsFieldName + "." + cropId + ".textSizes"),
                                        "type", "text",
                                        "value", crop.getTextSizes());
                            page.writeEnd();
                            page.writeStart("td");
                                page.writeTag("input",
                                        "name", page.h(cropsFieldName + "." + cropId + ".textXs"),
                                        "type", "text",
                                        "value", crop.getTextXs());
                            page.writeEnd();
                            page.writeStart("td");
                                page.writeTag("input",
                                        "name", page.h(cropsFieldName + "." + cropId + ".textYs"),
                                        "type", "text",
                                        "value", crop.getTextYs());
                            page.writeEnd();
                            page.writeStart("td");
                                page.writeTag("input",
                                        "name", page.h(cropsFieldName + "." + cropId + ".textWidths"),
                                        "type", "text",
                                        "value", crop.getTextWidths());
                            page.writeEnd();

                        //end tr from if/else
                        page.writeEnd();
                    }
                page.writeEnd();
            page.writeEnd();
        page.writeEnd();
    }

    private static void writeImageEditorImage(ToolPageContext page, StorageItem fieldValue) throws IOException {

        String fieldValueUrl;
        String resizeScale = "";

        if (ImageEditor.Static.getDefault() != null) {
           ImageTag.Builder imageTagBuilder = new ImageTag.Builder(fieldValue)
                   .setWidth(1000)
                   .setResizeOption(ResizeOption.ONLY_SHRINK_LARGER)
                   .setEdits(false);
            Number originalWidth = null;
            if (!ObjectUtils.isBlank(CollectionUtils.getByPath(imageTagBuilder.getItem().getMetadata(), "image/originalWidth"))) {
                originalWidth = (Number) CollectionUtils.getByPath(imageTagBuilder.getItem().getMetadata(), "image/originalWidth");
            } else if (!ObjectUtils.isBlank(CollectionUtils.getByPath(imageTagBuilder.getItem().getMetadata(), "dims/originalWidth"))) {
                originalWidth = (Number) CollectionUtils.getByPath(imageTagBuilder.getItem().getMetadata(), "dims/originalWidth");
            } else if (!ObjectUtils.isBlank(CollectionUtils.getByPath(imageTagBuilder.getItem().getMetadata(), "width"))) {
                originalWidth = (Number) CollectionUtils.getByPath(imageTagBuilder.getItem().getMetadata(), "width");
            }
            if (originalWidth != null) {
                if (originalWidth.intValue() > 1000) {
                    resizeScale = String.format("%.2f", (double) 1000 / originalWidth.intValue());
                }
            }
            fieldValueUrl = imageTagBuilder.toUrl();
        } else {
            fieldValueUrl = fieldValue.getPublicUrl();
        }

        page.writeStart("div", "class", "imageEditor-image");
            page.writeTag("img",
                    "alt", "",
                    "data-scale", resizeScale,
                    "src", page.url("/misc/proxy.jsp",
                            "url", fieldValueUrl,
                            "hash", StringUtils.hex(StringUtils.hmacSha1(Settings.getSecret(), fieldValueUrl))));
        page.writeEnd();
    }

    public static void processForm(ToolPageContext page) throws IOException, ServletException {

        //TODO: split up form processing. Some of this belongs in FilePreview
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
        String cropsName = inputName + ".crops.";

        String brightnessName = inputName + ".brightness";
        String contrastName = inputName + ".contrast";
        String flipHName = inputName + ".flipH";
        String flipVName = inputName + ".flipV";
        String grayscaleName = inputName + ".grayscale";
        String invertName = inputName + ".invert";
        String rotateName = inputName + ".rotate";
        String sepiaName = inputName + ".sepia";
        String sharpenName = inputName + ".sharpen";
        String blurName = inputName + ".blur";

        String metadataFieldName = fieldName + ".metadata";
        String cropsFieldName = fieldName + ".crops";

        Map<String, Object> fieldValueMetadata = null;
        if (fieldValue != null) {
            fieldValueMetadata = fieldValue.getMetadata();
        }

        if (fieldValueMetadata == null) {
            fieldValueMetadata = new LinkedHashMap<String, Object>();
        }

        Map<String, Object> edits = (Map<String, Object>) fieldValueMetadata.get("cms.edits");

        if (edits == null) {
            edits = new HashMap<String, Object>();
            fieldValueMetadata.put("cms.edits", edits);
        }

        double brightness = ObjectUtils.to(double.class, edits.get("brightness"));
        double contrast = ObjectUtils.to(double.class, edits.get("contrast"));
        boolean flipH = ObjectUtils.to(boolean.class, edits.get("flipH"));
        boolean flipV = ObjectUtils.to(boolean.class, edits.get("flipV"));
        boolean grayscale = ObjectUtils.to(boolean.class, edits.get("grayscale"));
        boolean invert = ObjectUtils.to(boolean.class, edits.get("invert"));
        int rotate = ObjectUtils.to(int.class, edits.get("rotate"));
        boolean sepia = ObjectUtils.to(boolean.class, edits.get("sepia"));
        int sharpen = ObjectUtils.to(int.class, edits.get("sharpen"));

        List<String> blurs = new ArrayList<String>();
        if (!ObjectUtils.isBlank(edits.get("blur"))) {
            Object blur = edits.get("blur");
            if (blur instanceof String && ObjectUtils.to(String.class, blur).matches("(\\d+x){3}\\d+")) {
                blurs.add(ObjectUtils.to(String.class, blur));
            } else if (blur instanceof List) {
                for (Object blurItem : (List) blur) {
                    String blurValue = ObjectUtils.to(String.class, blurItem);
                    if (blurValue.matches("(\\d+x){3}\\d+")) {
                        blurs.add(blurValue);
                    }
                }
            }
        }

        Map<String, ImageCrop> crops = ObjectUtils.to(new TypeReference<Map<String, ImageCrop>>() { }, fieldValueMetadata.get("cms.crops"));
        if (crops == null) {
            // for backward compatibility
            crops = ObjectUtils.to(new TypeReference<Map<String, ImageCrop>>() { }, state.getValue(cropsFieldName));
        }
        if (crops == null) {
            crops = new HashMap<String, ImageCrop>();
        }

        crops = new TreeMap<String, ImageCrop>(crops);

        Map<String, StandardImageSize> sizes = new HashMap<String, StandardImageSize>();
        for (StandardImageSize size : StandardImageSize.findAll()) {
            String sizeId = size.getId().toString();
            sizes.put(sizeId, size);
            if (crops.get(sizeId) == null) {
                crops.put(sizeId, new ImageCrop());
            }
        }

        File file = null;

        try {
            String action = page.param(actionName);
            StorageItem newItem = null;

            brightness = page.param(double.class, brightnessName);
            contrast = page.param(double.class, contrastName);
            flipH = page.param(boolean.class, flipHName);
            flipV = page.param(boolean.class, flipVName);
            grayscale = page.param(boolean.class, grayscaleName);
            invert = page.param(boolean.class, invertName);
            rotate = page.param(int.class, rotateName);
            sepia = page.param(boolean.class, sepiaName);
            sharpen = page.param(int.class, sharpenName);

            edits = new HashMap<String, Object>();

            if (brightness != 0.0) {
                edits.put("brightness", brightness);
            }
            if (contrast != 0.0) {
                edits.put("contrast", contrast);
            }
            if (flipH) {
                edits.put("flipH", flipH);
            }
            if (flipV) {
                edits.put("flipV", flipV);
            }
            if (invert) {
                edits.put("invert", invert);
            }
            if (rotate != 0) {
                edits.put("rotate", rotate);
            }
            if (grayscale) {
                edits.put("grayscale", grayscale);
            }
            if (sepia) {
                edits.put("sepia", sepia);
            }
            if (sharpen != 0) {
                edits.put("sharpen", sharpen);
            }

            if (!ObjectUtils.isBlank(page.params(String.class, blurName))) {
                blurs = new ArrayList<String>();
                for (String blur : page.params(String.class, blurName)) {
                    if (!blurs.contains(blur)) {
                        blurs.add(blur);
                    }
                }

                if (blurs.size() == 1) {
                    edits.put("blur", blurs.get(0));
                } else {
                    edits.put("blur", blurs);
                }
            }

            fieldValueMetadata.put("cms.edits", edits);

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

            // Standard sizes.
            for (Iterator<Map.Entry<String, ImageCrop>> i = crops.entrySet().iterator(); i.hasNext();) {
                Map.Entry<String, ImageCrop> e = i.next();
                String cropId = e.getKey();
                double x = page.doubleParam(cropsName + cropId + ".x");
                double y = page.doubleParam(cropsName + cropId + ".y");
                double width = page.doubleParam(cropsName + cropId + ".width");
                double height = page.doubleParam(cropsName + cropId + ".height");
                String texts = page.param(cropsName + cropId + ".texts");
                String textSizes = page.param(cropsName + cropId + ".textSizes");
                String textXs = page.param(cropsName + cropId + ".textXs");
                String textYs = page.param(cropsName + cropId + ".textYs");
                String textWidths = page.param(cropsName + cropId + ".textWidths");
                if (x != 0.0 || y != 0.0 || width != 0.0 || height != 0.0 || !ObjectUtils.isBlank(texts)) {
                    ImageCrop crop = e.getValue();
                    crop.setX(x);
                    crop.setY(y);
                    crop.setWidth(width);
                    crop.setHeight(height);
                    crop.setTexts(texts);
                    crop.setTextSizes(textSizes);
                    crop.setTextXs(textXs);
                    crop.setTextYs(textYs);
                    crop.setTextWidths(textWidths);

                    for (Iterator<ImageTextOverlay> j = crop.getTextOverlays().iterator(); j.hasNext();) {
                        ImageTextOverlay textOverlay = j.next();
                        String text = textOverlay.getText();

                        if (text != null) {
                            StringBuilder cleaned = new StringBuilder();

                            for (Object item : new ReferentialText(text, true)) {
                                if (item instanceof String) {
                                    cleaned.append((String) item);
                                }
                            }

                            text = cleaned.toString();

                            if (ObjectUtils.isBlank(text.replaceAll("<[^>]*>", ""))) {
                                j.remove();

                            } else {
                                textOverlay.setText(text);
                            }
                        }
                    }

                } else {
                    i.remove();
                }
            }
            fieldValueMetadata.put("cms.crops", crops);
            // Removes legacy cropping information
            if (state.getValue(cropsFieldName) != null) {
                state.remove(cropsFieldName);
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
            return;

        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }

    private static enum ImageAdjustment {

        BRIGHTNESS("brightness", -1.0, 1.0, 0.01, double.class),
        CONTRAST("contrast", -1.0, 1.0, 0.01, double.class),
        FLIP_H("flipH"),
        FLIP_V("flipV"),
        INVERT("invert"),
        GRAYSCALE("grayscale"),
        ROTATE("rotate", -90.0, 90.0, 90.0, int.class),
        SEPIA("sepia"),
        SHARPEN("sharpen", true),
        BLUR("blur");

        private String title;
        private String type;
        private String inputType;
        private Class valueType;
        private boolean javaImageEditorOnly = false;
        private double min;
        private double max;
        private double step;

        ImageAdjustment(String title) {
            this.title = title;
            this.inputType = "checkbox";
            this.valueType = boolean.class;
            this.javaImageEditorOnly = false;
        }

        ImageAdjustment(String title, boolean javaImageEditorOnly) {
            this.title = title;
            this.inputType = "checkbox";
            this.valueType = boolean.class;
            this.javaImageEditorOnly = javaImageEditorOnly;
        }

        ImageAdjustment(String title, double min, double max, double step, Class valueType) {
            this.title = title;
            this.min = min;
            this.max = max;
            this.step = step;
            this.inputType = "range";
            this.valueType = valueType;
        }
    }
}
