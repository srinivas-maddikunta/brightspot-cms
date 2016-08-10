package com.psddev.cms.rte;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class RichTextToolbarStyle extends RichTextToolbarButton {

    public static final RichTextToolbarStyle BOLD = builder()
            .style("bold")
            .text("B")
            .cssClass("rte2-toolbar-bold")
            .tooltip("Bold")
            .inline(true)
            .build();

    public static final RichTextToolbarStyle ITALIC = builder()
            .style("italic")
            .text("I")
            .cssClass("rte2-toolbar-italic")
            .tooltip("Italic")
            .inline(true)
            .build();

    public static final RichTextToolbarStyle UNDERLINE = builder()
            .style("underline")
            .text("U")
            .cssClass("rte2-toolbar-underline")
            .tooltip("Underline")
            .inline(true)
            .build();

    public static final RichTextToolbarStyle STRIKETHROUGH = builder()
            .style("strikethrough")
            .text("S")
            .cssClass("rte2-toolbar-strikethrough")
            .tooltip("Strikethrough")
            .inline(true)
            .build();

    public static final RichTextToolbarStyle SUPERSCRIPT = builder()
            .style("superscript")
            .text("Super")
            .cssClass("rte2-toolbar-superscript")
            .tooltip("Superscript")
            .inline(true)
            .build();

    public static final RichTextToolbarStyle SUBSCRIPT = builder()
            .style("subscript")
            .text("Sub")
            .cssClass("rte2-toolbar-subscript")
            .tooltip("Subscript")
            .inline(true)
            .build();

    public static final RichTextToolbarStyle LINK = builder()
            .style("link")
            .text("Link")
            .cssClass("rte2-toolbar-link")
            .tooltip("Link")
            .inline(true)
            .build();

    public static final RichTextToolbarStyle HTML = builder()
            .style("html")
            .text("HTML")
            .cssClass("rte2-toolbar-html")
            .tooltip("Raw HTML")
            .inline(true)
            .build();

    public static final RichTextToolbarStyle UL = builder()
            .style("ul")
            .text("&bull;")
            .cssClass("rte2-toolbar-ul")
            .tooltip("Bulleted List")
            .build();

    public static final RichTextToolbarStyle OL = builder()
            .style("ol")
            .text("1.")
            .cssClass("rte2-toolbar-ol")
            .tooltip("Numbered List")
            .build();

    public static final RichTextToolbarStyle ALIGN_LEFT = builder()
            .style("alignLeft")
            .activeIfUnset("alignCenter", "alignRight", "ol", "ul")
            .text("Left")
            .cssClass("rte2-toolbar-align-left")
            .tooltip("Left Align Text")
            .build();

    public static final RichTextToolbarStyle ALIGN_CENTER = builder()
            .style("alignCenter")
            .text("Center")
            .cssClass("rte2-toolbar-align-center")
            .tooltip("Center Align Text")
            .build();

    public static final RichTextToolbarStyle ALIGN_RIGHT = builder()
            .style("alignRight")
            .text("Right")
            .cssClass("rte2-toolbar-align-right")
            .tooltip("Right Align Text")
            .build();

    public static final RichTextToolbarStyle COMMENT = builder()
            .style("comment")
            .text("Add Comment")
            .cssClass("rte2-toolbar-comment")
            .tooltip("Add Comment")
            .inline(true)
            .build();

    private String style;
    private Set<String> activeIfUnset;

    public static Builder builder() {
        return new Builder();
    }

    protected RichTextToolbarStyle() {
    }

    public String getStyle() {
        return style;
    }

    public Set<String> getActiveIfUnset() {
        return activeIfUnset;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("style", getStyle());
        map.put("activeIfUnset", getActiveIfUnset());
        return map;
    }

    public static class Builder extends ButtonBuilder<Builder, RichTextToolbarStyle> {

        protected Builder() {
            super(new RichTextToolbarStyle());
        }

        public Builder style(String style) {
            item.style = style;
            return this;
        }

        public Builder activeIfUnset(String... styles) {
            if (styles != null) {
                Set<String> set = new HashSet<>();
                Collections.addAll(set, styles);
                item.activeIfUnset = set;
            }

            return this;
        }

        @Override
        protected void verify() {
            super.verify();
            Objects.requireNonNull(item.style);
        }
    }
}
