package com.psddev.cms.rte;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public interface RichTextToolbarItem {

    RichTextToolbarItem CUSTOM = () -> ImmutableMap.of("custom", true);

    RichTextToolbarItem ELEMENTS = () -> ImmutableMap.of("richTextElements", true);

    Map<String, Object> toMap();
}
