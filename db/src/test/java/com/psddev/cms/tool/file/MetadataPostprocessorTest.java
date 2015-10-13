package com.psddev.cms.tool.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import com.psddev.dari.util.StorageItem;
import com.psddev.dari.util.StorageItemPart;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MetadataPostprocessorTest {

    @Mock
    StorageItemPart part;

    @Mock
    StorageItem item;

    MetadataPostprocessor processor;

    @Before
    public void before() {
        processor = new MetadataPostprocessor();
    }

    @Test
    public void nullPart() {
        part = null;
        processor.process(part);
    }

    @Test
    public void nullStorageItem() {
        when(part.getStorageItem()).thenReturn(null);
        processor.process(part);
    }

    @Test
    public void imageStorageItem() throws URISyntaxException, IOException {
        Map<String, Object> metadata = new HashMap<>();
        when(part.getStorageItem()).thenReturn(item);
        when(item.getMetadata()).thenReturn(metadata);
        when(item.getContentType()).thenReturn("image/png");

        File file = new File(getClass().getClassLoader().getResource("com/psddev/cms/tool/file/MetadataPostprocessor_Test/test.png").toURI());
        when(item.getData()).thenReturn(new FileInputStream(file));

        processor.process(part);

        verify(item, Mockito.times(1)).getData();
    }

    @Test
    public void metadataException() throws IOException {
        when(part.getStorageItem()).thenReturn(item);

        Map<String, Object> metadata = new HashMap<>();
        when(item.getMetadata()).thenReturn(metadata);
        when(item.getContentType()).thenReturn("image/png");
        when(item.getData()).thenThrow(new IOException());

        processor.process(part);
    }
}
