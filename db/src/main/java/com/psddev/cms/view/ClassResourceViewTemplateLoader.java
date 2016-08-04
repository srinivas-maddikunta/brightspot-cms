package com.psddev.cms.view;

import java.io.IOException;
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
        URL templateUrl = context.getResource(path);

        if (templateUrl == null) {
            throw new IOException(String.format(
                    "Unable to find template given a Class context of [%s] for path [%s]",
                    context.getClass(),
                    path));
        }

        return templateUrl;
    }
}
