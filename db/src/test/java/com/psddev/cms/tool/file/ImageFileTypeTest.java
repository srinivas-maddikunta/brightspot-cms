package com.psddev.cms.tool.file;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.psddev.cms.db.ImageCrop;
import com.psddev.cms.db.StandardImageSize;
import com.psddev.cms.tool.FileContentType;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.AbstractDatabase;
import com.psddev.dari.db.ColorDistribution;
import com.psddev.dari.db.Database;
import com.psddev.dari.db.DatabaseEnvironment;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.State;
import com.psddev.dari.util.ImageEditor;
import com.psddev.dari.util.JavaImageEditor;
import com.psddev.dari.util.PaginatedResult;
import com.psddev.dari.util.Settings;
import com.psddev.dari.util.StorageItem;
import com.psddev.dari.util.UuidUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class ImageFileTypeTest {

    @RunWith(MockitoJUnitRunner.class)
    public static class PriorityTest {

        ImageFileType imageFileType;

        @Mock
        StorageItem storageItem;

        @Before
        public void before() {
            imageFileType = new ImageFileType();
        }

        @Test
        public void defaultPriorityTest() {
            when(storageItem.getContentType()).thenReturn("image/png");
            assertTrue(imageFileType.getPriority(storageItem) == FileContentType.DEFAULT_PRIORITY_LEVEL);
        }

        @Test
        public void negativePriorityTest() {
            when(storageItem.getContentType()).thenReturn("video/mp4");
            assertTrue(imageFileType.getPriority(storageItem) < FileContentType.DEFAULT_PRIORITY_LEVEL);
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class PreviewTest {

        private static final String IMAGE_PATH = "/image/file.png";
        private static final String FIELD_NAME = "file";
        private static final Map<String, Object> METADATA;
        private static final Map<String, Object> EDITS;
        private static final Map<String, ImageCrop> CROPS;
        private static final Map<String, Double> FOCUS;
        private static final Double FOCUS_X = 0.7;
        private static final Double FOCUS_Y = 0.7;
        private static final String BRIGHTNESS = "0.7";
        private static final String CONTRAST = "0.7";
        private static final String FLIP_H = "true";
        private static final String FLIP_V = "true";
        private static final String GRAYSCALE = "true";
        private static final String INVERT = "true";
        private static final String ROTATE = "90";
        private static final String SEPIA = "true";
        private static final ImageCrop CROP_1;
        private static final ImageCrop CROP_2;

        static {
            METADATA = new HashMap<>();

            EDITS = new HashMap<>();
            CROPS = new LinkedHashMap<>();
            FOCUS = new HashMap<>();

            METADATA.put("cms.edits", EDITS);
            METADATA.put("cms.crops", CROPS);
            METADATA.put("cms.focus", FOCUS);

            EDITS.put("brightness", BRIGHTNESS);
            EDITS.put("contrast", CONTRAST);
            EDITS.put("flipH", FLIP_H);
            EDITS.put("flipV", FLIP_V);
            EDITS.put("grayscale", GRAYSCALE);
            EDITS.put("invert", INVERT);
            EDITS.put("rotate", ROTATE);
            EDITS.put("sepia", SEPIA);

            CROP_1 = new ImageCrop();
            CROP_1.setX(0.7);
            CROP_1.setY(0.1);
            CROP_1.setWidth(100);
            CROP_1.setHeight(200);

            CROP_2 = new ImageCrop();
            CROP_2.setX(0.8);
            CROP_2.setY(0.2);
            CROP_2.setWidth(200);
            CROP_2.setHeight(100);

            CROPS.put(StandardImageSizeDatabase.SIZE1.getId().toString(), CROP_1);
            CROPS.put(StandardImageSizeDatabase.SIZE2.getId().toString(), CROP_2);

            FOCUS.put("x", FOCUS_X);
            FOCUS.put("y", FOCUS_Y);
        }

        ToolPageContext page;
        StringWriter writer;

        @Mock
        StorageItem storageItem;

        @Mock
        State state;

        @Mock
        HttpServletRequest request;

        @Before
        public void before() throws IOException {
            when(storageItem.getContentType()).thenReturn("image/jpg");
            doReturn(IMAGE_PATH).when(storageItem).getPublicUrl();
            doReturn(METADATA).when(storageItem).getMetadata();

            ColorDistribution.Data data = mock(ColorDistribution.Data.class);
            doReturn(null).when(data).getDistribution();
            doReturn(data).when(state).as(any());

            // Use Mock Database
            Database database = spy(StandardImageSizeDatabase.class);
            DatabaseEnvironment environment = mock(DatabaseEnvironment.class);
            doReturn(environment).when(database).getEnvironment();
            Database.Static.overrideDefault(database);

            // Override ImageEditor
            String imageEditorName = "test";
            Settings.setOverride(ImageEditor.DEFAULT_IMAGE_EDITOR_SETTING, imageEditorName);
            String imageEditorPrefix = ImageEditor.SETTING_PREFIX + "/" + imageEditorName + "/";
            Settings.setOverride(imageEditorPrefix + "class", TestImageEditor.class.getName());

            // Prepare ToolPageContext
            page = spy(new ToolPageContext((ServletContext) null, null, null));

            when(request.getParameter(eq("inputName"))).thenReturn(FIELD_NAME);
            doReturn(request).when(page).getRequest();

            writer = new StringWriter();
            doReturn(new PrintWriter(writer)).when(page).getDelegate();
            doReturn("test").when(page).localize(any(), any());
        }

        @After
        public void after() {
            Database.Static.restoreDefault();
        }

        @Test
        public void noPreview() throws IOException, ServletException {
            new ImageFileType().writePreview(page, null, null);
            verify(page, never()).write();
            verify(page, never()).writeStart(any(), anyVararg());
            verify(page, never()).writeEnd();
        }

        @Test
        public void validatePreview() throws IOException, ServletException {

            // Writes image preview to writer
            new ImageFileType().writePreview(page, state, storageItem);

            Document doc = Jsoup.parse(writer.toString());

            // Verify Tools
            Element toolsContainer = doc.select(".imageEditor-tools").first();
            assertNotNull(toolsContainer);
            assertTrue(toolsContainer.select(".action-preview").size() == 1);
            assertTrue(toolsContainer.select(".icon-crop").size() == 1);
            assertTrue(toolsContainer.select(".icon-tint").size() == 0);

            // Verify Edit Inputs
            Element editsContainer = doc.select(".imageEditor-edit").first();
            assertNotNull(editsContainer);
            validateEditInput(editsContainer, FIELD_NAME + ".brightness", BRIGHTNESS);
            validateEditInput(editsContainer, FIELD_NAME + ".contrast", CONTRAST);
            validateEditInput(editsContainer, FIELD_NAME + ".flipH", FLIP_H);
            validateEditInput(editsContainer, FIELD_NAME + ".flipV", FLIP_V);
            validateEditInput(editsContainer, FIELD_NAME + ".invert", INVERT);
            validateEditInput(editsContainer, FIELD_NAME + ".grayscale", GRAYSCALE);
            validateEditInput(editsContainer, FIELD_NAME + ".rotate", ROTATE);
            validateEditInput(editsContainer, FIELD_NAME + ".sepia", SEPIA);

            // Verify Image
            Element imageContainer = doc.select(".imageEditor-image").first();
            assertNotNull(imageContainer);
            assertTrue(imageContainer.select("img").size() == 1);

            validateEditInput(imageContainer, FIELD_NAME + ".focusX", FOCUS_X.toString());
            validateEditInput(imageContainer, FIELD_NAME + ".focusY", FOCUS_Y.toString());

            // Validate Sizes
            Element sizesContainer = doc.select(".imageEditor-sizes").first();
            assertNotNull(sizesContainer);
            Elements sizeRows = sizesContainer.select("tr");
            assertTrue(sizeRows.size() == 2);


            validateCropInputs(sizeRows.get(0), StandardImageSizeDatabase.SIZE1, CROP_1);
            validateCropInputs(sizeRows.get(1), StandardImageSizeDatabase.SIZE2, CROP_2);
        }

        private void validateEditInput(Element container, String inputName, String expectedValue) {
            Element input = container.select("input[name=" + inputName + "]").first();
            validateInput(input, expectedValue);
        }

        private void validateCropInputs(Element cropInputContainer, StandardImageSize expectedSize, ImageCrop expectedCrop) {
            validateInput(cropInputContainer.select("input[name$=" + expectedSize.getId() + ".x]").first(), Double.toString(expectedCrop.getX()));
            validateInput(cropInputContainer.select("input[name$=" + expectedSize.getId() + ".y]").first(), Double.toString(expectedCrop.getY()));
            validateInput(cropInputContainer.select("input[name$=" + expectedSize.getId() + ".width]").first(), Double.toString(expectedCrop.getWidth()));
            validateInput(cropInputContainer.select("input[name$=" + expectedSize.getId() + ".height]").first(), Double.toString(expectedCrop.getHeight()));

            //TODO add check for crop texts

        }

        private void validateInput(Element input, String expectedValue) {
            assertNotNull(input);
            assertEquals(input.attr("value"), expectedValue);
        }
    }

    @Ignore
    public static class StandardImageSizeDatabase extends AbstractDatabase<Object> {

        public static final StandardImageSize SIZE1;
        public static final StandardImageSize SIZE2;

        static {
            SIZE1 = new StandardImageSize();
            SIZE1.setDisplayName("size1");
            SIZE1.setInternalName("size1");
            SIZE1.setWidth(100);
            SIZE1.setHeight(100);
            SIZE1.getState().setId(UuidUtils.createSequentialUuid());

            SIZE2 = new StandardImageSize();
            SIZE2.setDisplayName("size2");
            SIZE2.setInternalName("size2");
            SIZE2.setWidth(100);
            SIZE2.setHeight(100);
            SIZE2.getState().setId(UuidUtils.createSequentialUuid());
        }

        @Override
        public Object openConnection() {
            return null;
        }

        @Override
        public void closeConnection(Object o) {

        }

        @Override
        protected void doInitialize(String s, Map<String, Object> map) {

        }

        @Override
        public Date readLastUpdate(Query<?> query) {
            return null;
        }

        @Override
        public <T> PaginatedResult<T> readPartial(Query<T> query, long l, int i) {
            List<T> result = new ArrayList<>();

            result.add((T) SIZE1);
            result.add((T) SIZE2);

            return new PaginatedResult<>(l, i, result);
        }
    }

    @Ignore
    public static class TestImageEditor extends JavaImageEditor {

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void setName(String s) {

        }

        @Override
        public StorageItem edit(StorageItem storageItem, String s, Map<String, Object> map, Object... objects) {
            return storageItem;
        }

        @Override
        public void initialize(String s, Map<String, Object> map) {

        }
    }
}
