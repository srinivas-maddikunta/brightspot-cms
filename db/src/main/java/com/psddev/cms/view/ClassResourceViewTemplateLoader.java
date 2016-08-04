package com.psddev.cms.view;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Implementation of {@link UrlViewTemplateLoader} for loading templates
 * from resources, given a {@code Class} for context.
 */
public class ClassResourceViewTemplateLoader extends UrlViewTemplateLoader {

    private Class<?> context;

    public ClassResourceViewTemplateLoader(Class<?> context) {
        this.context = context;
    }

    @Override
    protected URL getTemplateUrl(String path) throws IOException {
        return context.getResource(path);
    }

    @Override
    public InputStream getTemplate(String path) throws IOException {
        return context.getResourceAsStream(path);
    }
}
