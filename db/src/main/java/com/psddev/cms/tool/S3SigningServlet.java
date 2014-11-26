package com.psddev.cms.tool;

import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.RoutingFilter;
import com.psddev.dari.util.Settings;
import com.psddev.dari.util.StringUtils;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RoutingFilter.Path(application = "cms", value = "s3auth")
public class S3SigningServlet extends HttpServlet {

    private static final String TO_SIGN_PARAM = "to_sign";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String secret = ObjectUtils.to(String.class, Settings.get("dari/storage/psddevS3/secret"));

        if (StringUtils.isBlank(secret)) {
            throw new ServletException("dari/storage/psddevS3/secret not found in your context.xml");
        }

        byte[] rawHmac = StringUtils.hmacSha1(secret, request.getParameter(TO_SIGN_PARAM));
        String result = new String(Base64.encodeBase64(rawHmac));

        response.setContentType("text/html");
        response.getWriter().write(result);
    }
}
