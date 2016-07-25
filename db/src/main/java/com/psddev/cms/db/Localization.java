package com.psddev.cms.db;

import com.psddev.cms.tool.AuthenticationFilter;
import com.psddev.dari.util.PageContextFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public final class Localization {

    public static String text(Locale locale, Object context, String key) {
        LocalizationContext localizationContext = context instanceof LocalizationContext
                ? (LocalizationContext) context
                : new LocalizationContext(context, null);

        Locale defaultLocale = Locale.getDefault();

        if (locale == null) {
            locale = defaultLocale;
        }

        boolean firstTry = true;

        while (true) {
            String localized = localizationContext.text(locale, locale, key);

            if (localized == null && !defaultLocale.equals(locale)) {
                localized = localizationContext.text(defaultLocale, locale, key);
            }

            if (localized == null) {
                Locale usLocale = Locale.US;
                String usLanguage = usLocale.getLanguage();

                if (!usLanguage.equals(defaultLocale.getLanguage())
                        && !usLanguage.equals(locale.getLanguage())) {

                    localized = localizationContext.text(usLocale, locale, key);
                }
            }

            if (localized != null) {
                return localized;
            }

            if (firstTry) {
                firstTry = false;
                ObjectTypeResourceBundle.invalidateInstances();

            } else {
                return localizationContext.missingText(key);
            }
        }
    }

    public static String currentUserText(Object context, String key) {
        Locale locale = null;
        HttpServletRequest request = PageContextFilter.Static.getRequestOrNull();

        if (request != null) {
            ToolUser user = AuthenticationFilter.Static.getUser(request);

            if (user != null) {
                locale = user.getLocale();
            }
        }

        return text(locale, context, key);
    }

    // Prevent instantiation.
    private Localization() {
    }
}
