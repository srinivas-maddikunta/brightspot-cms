package com.psddev.cms.tool;

import com.psddev.dari.db.Application;
import com.psddev.dari.util.RoutingFilter;
import com.psddev.dari.util.SmsProvider;
import com.psddev.dari.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet to handle test sms requests to the phone number provided.
 */
@RoutingFilter.Path(application = "cms", value = "testSms")
public class TestSmsServlet extends HttpServlet {

    private static final String SUCCESS_RESPONSE = "Test message sent!";
    private static final String ERROR_RESPONSE = "Unable to send test message!";

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String phoneNumber = request.getParameter("phoneNumber");

        try {
            if (!StringUtils.isBlank(phoneNumber)) {
                String testSms = "This is a test message.";
                String siteUrl = Application.Static.getInstance(CmsTool.class).getDefaultToolUrl();
                testSms += !StringUtils.isBlank(siteUrl) ? "\nCMS Url: " + siteUrl : "";
                SmsProvider.Static.getDefault().send(null, phoneNumber, testSms);
                response.getWriter().write(SUCCESS_RESPONSE);

            } else {
                response.getWriter().write(ERROR_RESPONSE);
            }

        } catch (IllegalStateException e) {
            response.getWriter().write(ERROR_RESPONSE);
        }
    }
}
