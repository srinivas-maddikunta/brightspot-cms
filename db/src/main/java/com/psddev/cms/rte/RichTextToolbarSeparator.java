package com.psddev.cms.rte;

import java.util.Map;

public class RichTextToolbarSeparator extends AbstractRichTextToolbarItem {

    public static final RichTextToolbarSeparator BLOCK = builder().inline(false).build();

    public static final RichTextToolbarSeparator INLINE = builder().inline(true).build();

    public static Builder builder() {
        return new Builder();
    }

    protected RichTextToolbarSeparator() {
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("separator", Boolean.TRUE);
        return map;
    }

    public static class Builder extends ItemBuilder<Builder, RichTextToolbarSeparator> {

        protected Builder() {
            super(new RichTextToolbarSeparator());
        }
    }
}
