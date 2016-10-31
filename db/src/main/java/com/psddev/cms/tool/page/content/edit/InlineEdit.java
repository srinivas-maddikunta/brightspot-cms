package com.psddev.cms.tool.page.content.edit;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.db.ToolUser;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.State;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.RoutingFilter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Creates inline edit form with applicable fields.
 */
@RoutingFilter.Path(application = "cms", value = "/content/inlineEdit")
public class InlineEdit extends PageServlet {

    @Override
    protected String getPermissionId() {
        return null;
    }

    @Override
    protected void doService(ToolPageContext page) throws IOException, ServletException {
        Object object = page.findOrReserve();

        if (object == null) {
            throw new IllegalArgumentException("No object found!");
        }

        Boolean error = null;

        if (page.isFormPost() && page.param(String.class, "action-publish") != null) {

            try {
                page.include("/WEB-INF/objectPost.jsp", "object", object);
                page.publish(object);
                error = Boolean.FALSE;

            } catch (Exception e) {
                error = Boolean.TRUE;
            }
        }

        writeForm(page, object, page.params(String.class, "f"), error);
    }

    private void writeForm(ToolPageContext page, Object object, List<String> fields, Boolean error) throws IOException, ServletException {
        State state = State.getInstance(object);
        UUID id = state.getId();
        ObjectType type = state.getType();

        String typeLabel = page.getTypeLabel(object);
        String iconName = "object";
        String buttonText = page.localize(type, "action.save");

        if (type != null) {
            ToolUi ui = type.as(ToolUi.class);
            iconName = ObjectUtils.firstNonBlank(ui.getIconName(), iconName);
            buttonText = ObjectUtils.firstNonBlank(
                    ui.getPublishButtonText(),
                    ui.isPublishable() ? page.localize(type, "action.publish") : buttonText);
        }

        Site site = page.getSite();
        ToolUser user = page.getUser();

        page.writeTag("!doctype html");
        page.writeStart("html",
                "class", site != null ? site.getCmsCssClass() : null,
                "data-user-id", user != null ? user.getId() : null,
                "data-user-label", user != null ? user.getLabel() : null,
                "data-time-zone", page.getUserDateTimeZone().getID(),
                "lang", MoreObjects.firstNonNull(user != null ? user.getLocale() : null, Locale.getDefault()).toLanguageTag()); {

            page.writeStart("head"); {
                page.writeStylesAndScripts();

                page.writeStart("script",
                        "type", "text/javascript",
                        "src", page.cmsUrl("/script/iframeResizer.contentWindow.js"))
                        .writeEnd();

                page.writeStart("style", "type", "text/css"); {
                    page.writeCss("body", "background", "transparent", "margin-left", "10px", "margin-right", "10px");
                    page.writeCss(".toolHeader", "display", "none");
                    page.writeCss(".toolContent", "background", "transparent");
                    page.writeCss(".widget.widget-content", "box-shadow", "none");
                }
                page.writeEnd();
            }
            page.writeEnd();

            page.writeStart("body"); {
                page.writeStart("div", "class", "toolHeader").writeEnd();
                page.writeStart("div", "class", "toolContent"); {

                    // Form
                    page.writeStart("form",
                            "class", "standardForm",
                            "method", "post",
                            "enctype", "multipart/form-data",
                            "action", page.url(""),
                            "autocomplete", "off",
                            "data-rtc-content-id", page.getCmsTool().isDisableFieldLocking() ? null : id,
                            "data-object-id", id,
                            "data-type", type != null ? type.getInternalName() : null); {

                        page.writeStart("div", "class", "contentForm-main inline"); {
                            page.writeStart("div", "class", "widget widget-content"); {

                                // Heading
                                page.writeStart("h1", "class", "breadcrumbs"); {
                                    page.writeStart("span", "class", "breadcrumbItem icon icon-" + iconName); {
                                        page.writeHtml(page.localize(
                                                InlineEdit.class,
                                                ImmutableMap.of("label", typeLabel),
                                                "title.heading"));
                                        page.writeHtml(": ");
                                        page.writeStart("span",
                                                "class", "ContentLabel",
                                                "data-dynamic-html", "${toolPageContext.createObjectLabelHtml(content)}"); {

                                            page.write(page.createObjectLabelHtml(object));
                                        }
                                        page.writeEnd();
                                    }
                                    page.writeEnd();
                                }
                                page.writeEnd();

                                // Full form button
                                page.writeStart("div", "class", "widgetControls"); {
                                    page.writeStart("a",
                                            "class", "icon icon-action-edit",
                                            "target", "_blank",
                                            "href", page.cmsUrl("/content/edit.jsp", "id", id)); {

                                        page.writeHtml(page.localize(InlineEdit.class, "action.fullForm"));
                                    }
                                    page.writeEnd();
                                }
                                page.writeEnd();

                                // Error message
                                if (Boolean.TRUE.equals(error)) {
                                    page.writeStart("div", "class", "message message-error"); {
                                        page.writeHtml(page.localize("com.psddev.cms.tool.page.content.Errors", "error.validation"));
                                    }
                                    page.writeEnd();

                                // Success message
                                } else if (Boolean.FALSE.equals(error)) {
                                    page.include("/WEB-INF/objectMessage.jsp", "object", object);
                                    page.writeStart("script", "type", "text/javascript"); {
                                        page.writeRaw("$('.Message-returnToDashboard').hide();");
                                    }
                                    page.writeEnd();
                                }

                                // Fields
                                page.writeSomeFormFields(object, false, fields.isEmpty() ? null : fields, null);

                                // Publish/Save button
                                page.writeStart("div", "class", "actions widget-publishingPublish"); {
                                    page.writeStart("button",
                                            "class", "icon icon-action-save",
                                            "name", "action-publish",
                                            "value", "true"); {

                                        page.writeHtml(buttonText);
                                    }
                                    page.writeEnd();
                                }
                                page.writeEnd();
                            }
                            page.writeEnd();
                        }
                        page.writeEnd();
                    }
                    page.writeEnd();
                }
                page.writeEnd();
            }
            page.writeEnd();
        }
        page.writeEnd();
    }
}
