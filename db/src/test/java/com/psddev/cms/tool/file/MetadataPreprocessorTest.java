package com.psddev.cms.tool.file;

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
import com.psddev.dari.util.StorageItem;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MetadataPreprocessorTest {

    @Mock
    FileItem fileItem;

    @Spy
    AbstractStorageItem storageItem;

    @Before
    public void before() {
        storageItem.setMetadata(null);
    }

    @Test
    public void verifyOriginalFileName() {
        String originalFilename = "test";

        when(fileItem.getName()).thenReturn(originalFilename);

        new MetadataPreprocessor().process(storageItem, fileItem);

        assertEquals(storageItem.getMetadata().get("originalFilename"), originalFilename);
    }

    @Test
    public void verifyHttpHeaders() {
        long fileSize = 100;
        String fileContentType = "image/jpeg";

        when(fileItem.getSize()).thenReturn(fileSize);
        when(fileItem.getContentType()).thenReturn(fileContentType);

        new MetadataPreprocessor().process(storageItem, fileItem);

        Map<String, Object> httpHeaders = (Map<String, Object>) storageItem.getMetadata().get("http.headers");

        assertEquals(((List<String>) httpHeaders.get("Cache-Control")).get(0), "public, max-age=31536000");
        assertEquals(((List<String>) httpHeaders.get("Content-Length")).get(0), String.valueOf(fileSize));
        assertEquals(((List<String>) httpHeaders.get("Content-Type")).get(0), fileContentType);
    }

}