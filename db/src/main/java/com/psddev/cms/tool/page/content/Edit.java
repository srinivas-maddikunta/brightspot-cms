package com.psddev.cms.tool.page.content;

import com.psddev.cms.db.Draft;
import com.psddev.cms.db.Overlay;
import com.psddev.cms.db.OverlayProvider;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.db.ToolUser;
import com.psddev.cms.db.WorkInProgress;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.State;
import com.psddev.dari.util.ObjectUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class Edit {

    public static Overlay getOverlay(Object content) {
        return content != null
                ? (Overlay) State.getInstance(content).getExtras().get("cms.tool.overlay")
                : null;
    }

    public static void writeOverlayProviderSelect(ToolPageContext page, Object content, OverlayProvider selected) throws IOException {
        List<OverlayProvider> overlayProviders = Query.from(OverlayProvider.class).selectAll();

        overlayProviders.removeIf(p -> !p.shouldOverlay(content));

        if (overlayProviders.isEmpty()) {
            return;
        }

        UUID contentId = State.getInstance(content).getId();

        page.writeStart("div", "class", "OverlayProviderSelect");
        page.writeStart("ul");
        {
            page.writeStart("li", "class", selected == null ? "selected" : null);
            {
                page.writeStart("a",
                        "href", page.url("",
                                "id", contentId,
                                "overlayId", null));
                page.writeHtml("Default");
                page.writeEnd();
            }
            page.writeEnd();

            for (OverlayProvider overlayProvider : overlayProviders) {
                page.writeStart("li", "class", overlayProvider.equals(selected) ? "selected" : null);
                {
                    page.writeStart("a",
                            "href", page.url("",
                                    "id", contentId,
                                    "overlayId", overlayProvider.getState().getId()));
                    page.writeObjectLabel(overlayProvider);
                    page.writeEnd();
                }
                page.writeEnd();
            }
        }
        page.writeEnd();
        page.writeEnd();
    }

    /**
     * Creates the placeholder text for the given {@code field} that should
     * be displayed to the user in the context of the given {@code page}.
     *
     * @param page Can't be {@code null}.
     * @param field Can't be {@code null}.
     * @return Never {@code null}.
     */
    public static String createPlaceholderText(ToolPageContext page, ObjectField field) throws IOException {
        String placeholder = field.as(ToolUi.class).getPlaceholder();

        if (field.isRequired()) {
            String required = page.localize(field.getParentType(), "placeholder.required");

            if (ObjectUtils.isBlank(placeholder)) {
                placeholder = required;

            } else {
                placeholder += ' ';
                placeholder += required;
            }
        }

        if (ObjectUtils.isBlank(placeholder)) {
            return "";

        } else {
            return placeholder;
        }
    }

    /**
     * Restores the work in progress associated with the given {@code content}
     * in the context of the given {@code page}.
     *
     * <p>If successful, writes an appropriate message to the output attached
     * to the given {@code page}.</p>
     *
     * @param page Can't be {@code null}.
     * @param content Can't be {@code null}.
     */
    public static void restoreWorkInProgress(ToolPageContext page, Object content) throws IOException {
        if (page.getOverlaidHistory(content) != null) {
            return;
        }

        State state = State.getInstance(content);

        if (state.hasAnyErrors()) {
            return;
        }

        ToolUser user = page.getUser();

        if (user.isDisableWorkInProgress()
                || page.getCmsTool().isDisableWorkInProgress()) {

            return;
        }

        WorkInProgress wip = Query.from(WorkInProgress.class)
                .where("owner = ?", user)
                .and("contentId = ?", state.getId())
                .first();

        if (wip == null) {
            return;
        }

        state.setValues(Draft.mergeDifferences(
                state.getDatabase().getEnvironment(),
                state.getSimpleValues(),
                wip.getDifferences()));

        page.writeStart("div", "class", "message message-warning WorkInProgressRestoredMessage");
        {
            page.writeStart("div", "class", "WorkInProgressRestoredMessage-actions");
            {
                page.writeStart("a",
                        "class", "icon icon-action-remove",
                        "href", page.cmsUrl("/user/wips",
                                "action-delete", true,
                                "wip", wip.getId(),
                                "returnUrl", page.url("")));
                page.writeHtml(page.localize(wip, "action.clearChanges"));
                page.writeEnd();
            }
            page.writeEnd();

            page.writeStart("p");
            {
                page.writeHtml(page.localize(wip, "message.restored"));
            }
            page.writeEnd();
        }
        page.writeEnd();
    }
}
