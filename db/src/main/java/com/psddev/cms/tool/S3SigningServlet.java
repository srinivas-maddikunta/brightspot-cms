package com.psddev.cms.tool;

import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.RoutingFilter;
import com.psddev.dari.util.Settings;
import com.psddev.dari.util.StringUtils;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SignatureException;

@RoutingFilter.Path(application = "cms", value = "s3auth")
public class S3SigningServlet extends HttpServlet {

    private static final String TO_SIGN_PARAM = "to_sign";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String secret = ObjectUtils.to(String.class, Settings.get("dari/storage/psddevS3/secret"));

        if (StringUtils.isBlank(secret)) {
            throw new ServletException("dari/storage/psddevS3/secret not found in your context.xml");
        }

        response.setContentType("text/html");
        try {
            response.getWriter().write(Signature.getSignature(request.getParameter(TO_SIGN_PARAM), secret));
        } catch (SignatureException e) {
            throw new ServletException("Unable to Sign request with your S3 secret");
        }
    }

    private static class Signature {

        private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

        public static String getSignature(String data, String key) throws SignatureException {
            String result;
            try {

                SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
                Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
                mac.init(signingKey);
                byte[] rawHmac = mac.doFinal(data.getBytes());
                result = new String(Base64.encodeBase64(rawHmac));

            } catch (Exception e) {
                throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
            }
            return result;
        }
    }
}
