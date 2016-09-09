package com.psddev.cms.rte;

import com.psddev.dari.util.CompactMap;

import java.util.Map;

public abstract class AbstractRichTextToolbarItem implements RichTextToolbarItem {

    protected boolean inline;

    public boolean isInline() {
        return inline;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new CompactMap<>();
        map.put("inline", isInline());
        return map;
    }

    protected abstract static class ItemBuilder<B extends ItemBuilder<B, I>, I extends AbstractRichTextToolbarItem> {

        protected final I item;

        protected ItemBuilder(I item) {
            this.item = item;
        }

        @SuppressWarnings("unchecked")
        public B inline(boolean inline) {
            item.inline = inline;
            return (B) this;
        }

        protected void verify() {
        }

        public I build() {
            verify();
            return item;
        }
    }
}
