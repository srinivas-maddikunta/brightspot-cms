package com.psddev.cms.tool.file;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.psddev.dari.util.StorageItemPart;
import com.psddev.dari.util.StorageItemPreprocessor;

public class MetadataPreprocessor implements StorageItemPreprocessor {

    @Override
    public void process(StorageItemPart part) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("originalFilename", part.getName());

        Map<String, List<String>> httpHeaders = new LinkedHashMap<>();
        httpHeaders.put("Cache-Control", Collections.singletonList("public, max-age=31536000"));
        httpHeaders.put("Content-Length", Collections.singletonList(String.valueOf(part.getSize())));
        httpHeaders.put("Content-Type", Collections.singletonList(part.getContentType()));
        metadata.put("http.headers", httpHeaders);

        part.getStorageItem().getMetadata().putAll(metadata);
    }
}
