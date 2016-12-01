package com.psddev.cms.tool;

import com.psddev.cms.db.Site;
import com.psddev.dari.db.Application;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.AbstractFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class CrossDomainFilter extends AbstractFilter {

    protected abstract void doCrossDomainRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws Exception;

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
        CmsTool cms = Application.Static.getInstance(CmsTool.class);

        // Set CORS header if cross domain is enabled and origin matches a site url.
        if (cms.isEnableCrossDomainInlineEditing()) {
            String origin = request.getHeader("origin");

            if (origin != null) {
                if (origin.endsWith("/")) {
                    origin = origin.substring(0, origin.length() - 1);
                }

                if (Query.from(Site.class).where("urls startsWith ?", origin).hasMoreThan(0)) {
                    response.setHeader("Access-Control-Allow-Origin", origin);
                }
            }
        }

        doCrossDomainRequest(request, response, chain);
    }
}
