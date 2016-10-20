package com.psddev.cms.tool.page.content.edit;

import com.google.common.collect.ImmutableMap;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.State;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.RoutingFilter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RoutingFilter.Path(application = "cms", value = "/content/inlineEdit")
public class InlineEdit extends PageServlet {

    @Override
    protected String getPermissionId() {
        return null;
    }

    @Override
    protected void doService(ToolPageContext page) throws IOException, ServletException {
        UUID id = page.param(UUID.class, "id");
        Object object = Query.fromAll().where("id = ?", id).first();

        if (object == null) {
            throw new IllegalArgumentException(String.format("No object with id [%s]!", id));
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
        ObjectType type = State.getInstance(object).getType();
        String typeLabel = page.getTypeLabel(object);

        String iconName = "object";
        String buttonText = page.localize(type, "action.save");

        if (type != null) {
            ToolUi ui = type.as(ToolUi.class);
            iconName = ObjectUtils.firstNonBlank(ui.getIconName(), iconName);
            buttonText = ObjectUtils.firstNonNull(
                    ui.getPublishButtonText(),
                    ui.isPublishable() ? page.localize(type, "action.publish") : buttonText);
        }

        page.writeHeader(typeLabel); {

            // Form
            page.writeStart("form",
                    "class", "standardForm",
                    "method", "post",
                    "enctype", "multipart/form-data",
                    "action", page.url(""),
                    "autocomplete", "off",
                    "data-type", type != null ? type.getInternalName() : null); {

                page.writeStart("div", "class", "contentForm-main"); {
                    page.writeStart("div", "class", "widget widget-content"); {

                        // Heading
                        page.writeStart("h1", "class", "icon icon-" + iconName); {
                            page.writeHtml(page.localize(
                                    InlineEdit.class,
                                    ImmutableMap.of("typeLabel", typeLabel, "objectLabel", page.createObjectLabelHtml(object)),
                                    "title.heading"));
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
                        }

                        // Fields
                        page.writeSomeFormFields(object, false, fields, null);

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
        page.writeFooter();
    }
}
