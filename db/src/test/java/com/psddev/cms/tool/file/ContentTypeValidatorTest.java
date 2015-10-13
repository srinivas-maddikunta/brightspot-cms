package com.psddev.cms.tool.file;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.psddev.dari.util.Settings;
import com.psddev.dari.util.SparseSet;
import com.psddev.dari.util.StorageItemPart;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContentTypeValidatorTest {

    private static final String RESOURCE_PATH_PREFIX = "com/psddev/cms/tool/file/ContentTypeValidator_Test/";

    @Mock
    File file;

    @Mock
    FileItem fileItem;

    ContentTypeValidator validator;
    StorageItemPart part;

    @Before
    public void before() {
        validator = new ContentTypeValidator();
        part = new StorageItemPart();
        part.setFile(file);
        part.setFileItem(fileItem);
    }

    @Test(expected = NullPointerException.class)
    public void nullPart() throws IOException {
        validator.validate(null);
    }

    @Test(expected = NullPointerException.class)
    public void nullFile() throws IOException {
        part.setFile(null);
        validator.validate(part);
    }

    @Test
    public void nullContentType() throws IOException {
        when(fileItem.getContentType()).thenReturn(null);
        validator.validate(part);
    }

    @Test(expected = IllegalStateException.class)
    public void invalidContentType() throws IOException, URISyntaxException {
        Settings.setOverride("cms/tool/fileContentTypeGroups", "+image/jpeg");
        when(fileItem.getContentType()).thenReturn("image/png");
        validator.validate(part);
    }

    @Test
    public void validContentType() throws IOException, URISyntaxException {
        String fileContentType = "image/png";

        when(fileItem.getContentType()).thenReturn("image/png");

        Set<String> contentTypeGroups = new SparseSet();
        contentTypeGroups.add(fileContentType);
        contentTypeGroups.add("text/html");
        Settings.setOverride("cms/tool/fileContentTypeGroups", contentTypeGroups.toString());

        validator.validate(part);
    }

    @Test(expected = IOException.class)
    public void disguisedHtmlFile() throws IOException, URISyntaxException {

        Settings.setOverride("cms/tool/fileContentTypeGroups", "+image/jpeg");

        String fileName = "html-image.jpg";
        when(fileItem.getContentType()).thenReturn("image/jpeg");
        part.setFile(new File(getClass().getClassLoader().getResource(RESOURCE_PATH_PREFIX + fileName).toURI()));

        validator.validate(part);
    }

    @Test
    public void validFile() throws IOException, URISyntaxException {
        Settings.setOverride("cms/tool/fileContentTypeGroups", "+image/png");

        String fileName = "test.png";
        when(fileItem.getContentType()).thenReturn("image/png");
        part.setFile(new File(getClass().getClassLoader().getResource(RESOURCE_PATH_PREFIX + fileName).toURI()));
        validator.validate(part);
    }
}