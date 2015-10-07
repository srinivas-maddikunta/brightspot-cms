package com.psddev.cms.tool.file;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import com.psddev.dari.util.StorageItem;
import com.psddev.dari.util.StorageItemPreprocessor;

public class MetadataPreprocessor implements StorageItemPreprocessor {

    @Override
    public void process(StorageItem storageItem, FileItem fileItem) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("originalFilename", fileItem.getName());

        Map<String, List<String>> httpHeaders = new LinkedHashMap<>();
        httpHeaders.put("Cache-Control", Collections.singletonList("public, max-age=31536000"));
        httpHeaders.put("Content-Length", Collections.singletonList(String.valueOf(fileItem.getSize())));
        httpHeaders.put("Content-Type", Collections.singletonList(fileItem.getContentType()));
        metadata.put("http.headers", httpHeaders);

        storageItem.getMetadata().putAll(metadata);
    }
}
