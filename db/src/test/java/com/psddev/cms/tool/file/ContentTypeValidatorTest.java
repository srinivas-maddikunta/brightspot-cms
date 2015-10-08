package com.psddev.cms.tool.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import com.psddev.dari.util.AbstractStorageItem;
import com.psddev.dari.util.Settings;
import com.psddev.dari.util.SparseSet;
import com.psddev.dari.util.StorageItem;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContentTypeValidatorTest {

    @Mock
    File file;

    ContentTypeValidator validator;

    @Before
    public void before() {
        validator = new ContentTypeValidator();
    }

    @Test(expected = NullPointerException.class)
    public void nullFile() throws IOException {
        validator.validate(null, "");
    }

    @Test(expected = IllegalStateException.class)
    public void invalidContentType() throws IOException {
        Settings.setOverride("cms/tool/fileContentTypeGroups", "+image/png");
        validator.validate(file, "image/jpeg");
    }

    @Test
    public void validContentType() throws IOException {
        String fileContentType = "image/jpeg";
        Set<String> contentTypeGroups = new SparseSet();
        contentTypeGroups.add(fileContentType);
        contentTypeGroups.add("text/html");
        Settings.setOverride("cms/tool/fileContentTypeGroups", contentTypeGroups.toString());
        validator.validate(file, fileContentType);
    }

    @Test(expected = IOException.class)
    public void disguisedHtmlFile() throws IOException, URISyntaxException {
        File htmlFile = new File(getClass().getClassLoader().getResource("html-image.jpg").toURI());
        Settings.setOverride("cms/tool/fileContentTypeGroups", "+image/jpeg");
        validator.validate(htmlFile, "image/jpeg");
    }
}