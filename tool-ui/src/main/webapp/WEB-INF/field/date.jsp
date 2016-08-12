<%@ page session="false" import="

com.psddev.cms.db.ToolUi,
com.psddev.cms.tool.ToolPageContext,

com.psddev.dari.db.ObjectField,
com.psddev.dari.db.State,

java.util.Date
" %><%

ToolPageContext wp = new ToolPageContext(pageContext);
State state = State.getInstance(request.getAttribute("object"));
ObjectField field = (ObjectField) request.getAttribute("field");
String inputName = (String) request.getAttribute("inputName");
String millisName = inputName + ".millis";
String fieldName = field.getInternalName();
Date fieldValue = (Date) state.getByPath(fieldName);

if (Boolean.TRUE.equals(request.getAttribute("isFormPost"))) {
    fieldValue = wp.param(Date.class, inputName);

    if (fieldValue != null) {
        fieldValue = new Date(fieldValue.getTime() + wp.param(int.class, millisName));
    }

    state.put(fieldName, fieldValue);
    return;
}

wp.writeStart("div", "class", "inputSmall");
    wp.writeElement("input",
            "type", "text",
            "class", "date",
            "name", inputName,
            "placeholder", field.as(ToolUi.class).getPlaceholder(),
            "value", fieldValue != null ? (fieldValue.getTime() / 1000L) * 1000L : null);

    wp.writeElement("input",
            "type", "hidden",
            "name", millisName,
            "value", fieldValue != null
                    ? fieldValue.getTime() % 1000
                    : 0);
wp.writeEnd();
%>
