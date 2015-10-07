package com.psddev.cms.tool.file;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import com.psddev.dari.util.AbstractStorageItem;
import com.psddev.dari.util.Settings;
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
        String fileContentType = "image/jpeg";
        Settings.setOverride("cms/tool/fileContentTypeGroups", fileContentType);
        validator.validate(file, fileContentType);
    }
}