package com.psddev.cms.rte;

import java.util.Map;
import java.util.Objects;

public abstract class RichTextToolbarButton extends AbstractRichTextToolbarItem {

    protected String text;
    protected String cssClass;
    protected String tooltip;

    public String getText() {
        return text;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getTooltip() {
        return tooltip;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("text", getText());
        map.put("className", getCssClass());
        map.put("tooltip", getTooltip());
        return map;
    }

    protected abstract static class ButtonBuilder<B extends ButtonBuilder<B, I>, I extends RichTextToolbarButton> extends ItemBuilder<B, I> {

        protected ButtonBuilder(I item) {
            super(item);
        }

        @SuppressWarnings("unchecked")
        public B text(String text) {
            item.text = text;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B cssClass(String cssClass) {
            item.cssClass = cssClass;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B tooltip(String tooltip) {
            item.tooltip = tooltip;
            return (B) this;
        }

        @Override
        protected void verify() {
            Objects.requireNonNull(item.text);
            Objects.requireNonNull(item.cssClass);
        }
    }
}
