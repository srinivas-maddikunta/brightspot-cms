package com.psddev.cms.view.servlet;

import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StorageItem;
import com.psddev.dari.util.StorageItemFilter;
import com.psddev.dari.util.StringUtils;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Populates a StorageItem with the data from the query parameter from an HTTP request. The
 * parameter fetched has the same name as the field it populates unless
 * otherwise specified.
 */
@ServletViewRequestAnnotationProcessorClass(HttpFileParameterProcessor.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HttpFileParameter {
    String value() default "";
}

class HttpFileParameterProcessor implements ServletViewRequestAnnotationProcessor<HttpFileParameter> {

    @Override
    public StorageItem getValue(HttpServletRequest request, String fieldName, HttpFileParameter annotation) {
        String parameterName = annotation.value();
        if (StringUtils.isBlank(parameterName)) {
            parameterName = fieldName;
        }
        try {
            return StorageItemFilter.getParameter(request, parameterName, null);
        } catch (IOException e) {
            LoggerFactory.getLogger(HttpFileParameterProcessor.class)
                    .error(e.getMessage());
            return null;
        }
    }
}
