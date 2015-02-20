package com.psddev.cms.tool.page;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;

import com.psddev.cms.tool.DashboardWidget;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Record;
import com.psddev.dari.util.RoutingFilter;
import com.psddev.dari.util.UuidUtils;

@RoutingFilter.Path(application = "cms", value = "/addWidget")
public class AddWidget extends PageServlet {

    @Override
    protected String getPermissionId() {
        return null;
    }

    @Override
    protected void doService(ToolPageContext page) throws IOException, ServletException {
        if (page.isFormPost()) {
            String objectId = page.param(String.class, ToolPageContext.OBJECT_ID_PARAMETER);
            UUID newWidgetTypeId = page.param(UUID.class, objectId + "/widget.typeId");
            ObjectType widgetType = ObjectType.getInstance(newWidgetTypeId);
            Record widgetInstance = (Record) widgetType.createObject(UuidUtils.createSequentialUuid());

            page.writeTag("meta", "name", "addWidget",
                    "content", page.cmsUrl("/dashboardWidget/user/" + widgetInstance.getClass().getName() + "/" + widgetInstance.getId() + "/", "save", "true"),
                    "data-updateUrl", page.cmsUrl("/misc/updateUserDashboard/", "action", "dashboardWidgets-add", "id", widgetInstance.getId()),
                    "data-column", page.param(int.class, "col"));
        } else {
            page.writeStart("div", "class", "widget");
                page.writeStandardForm(new NewWidget());
            page.writeEnd();
        }
    }

    public static class NewWidget extends Record {

        @Embedded
        private DashboardWidget widget;

        public DashboardWidget getWidget() {
            return widget;
        }

        public void setWidget(DashboardWidget widget) {
            this.widget = widget;
        }
    }
}
