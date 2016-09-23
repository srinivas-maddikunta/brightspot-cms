package com.psddev.cms.db;

import com.google.common.base.Preconditions;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.TimeZone;
import com.psddev.cms.tool.AuthenticationFilter;
import com.psddev.dari.util.PageContextFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
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

    /**
     * Localizes the given {@code timeMillis} in the given {@code timeZoneId}
     * using the given {@code locale} and {@code format}.
     *
     * @param locale Nullable. Default is {@link Locale#getDefault()}.
     * @param timeZoneId Nullable. Default is {@link java.util.TimeZone#getDefault()}.
     * @param format Nonnull.
     * @return Nonnull.
     */
    public static String date(Locale locale, String timeZoneId, long timeMillis, String format) {
        Preconditions.checkNotNull(format);

        if (locale == null) {
            locale = Locale.getDefault();
        }

        if (timeZoneId == null) {
            timeZoneId = java.util.TimeZone.getDefault().getID();
        }

        DateFormat dateFormat = DateFormat.getPatternInstance(format, locale);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        return dateFormat.format(new Date(timeMillis));
    }

    public static String currentUserText(Object context, String key) {
        ToolUser user = currentUser();
        Locale locale = user != null ? user.getLocale() : null;
        return text(locale, context, key);
    }

    private static ToolUser currentUser() {
        HttpServletRequest request = PageContextFilter.Static.getRequestOrNull();
        return request != null ? AuthenticationFilter.Static.getUser(request) : null;
    }

    /**
     * Localizes the given {@code timeMillis} in the current user's time zone
     * using the current user's preferred locale and the given {@code format}.
     *
     * @param format Nonnull.
     * @return Nonnull.
     */
    public static String currentUserDate(long timeMillis, String format) {
        Preconditions.checkNotNull(format);

        ToolUser user = currentUser();
        Locale locale;
        String timeZoneId;

        if (user != null) {
            locale = user.getLocale();
            timeZoneId = user.getTimeZone();

        } else {
            locale = null;
            timeZoneId = null;
        }

        return date(locale, timeZoneId, timeMillis, format);
    }

    // Prevent instantiation.
    private Localization() {
    }
}
