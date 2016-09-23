package com.psddev.cms.db;

import com.google.common.base.Preconditions;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.TimeZone;
import com.psddev.cms.tool.AuthenticationFilter;
import com.psddev.dari.util.PageContextFilter;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Locale;

public final class Localization {

    public static final String NOT_AVAILABLE_KEY = "label.notAvailable";

    public static final String DATE_AND_TIME_SKELETON = "_dateAndTime";

    public static final String DATE_ONLY_SKELETON = "_dateOnly";

    public static final String TIME_ONLY_SKELETON = "_timeOnly";

    private static final String ICU_DATE_SKELETON = "EEE MMM dd";
    private static final String ICU_DATE_WITH_YEAR_SKELETON = "EEE MMM dd yyyy";
    private static final String ICU_TIME_SKELETON = "hh:mm aa";

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
     * using the given {@code locale} and {@code skeleton}.
     *
     * @param locale Nullable. Default is {@link Locale#getDefault()}.
     * @param timeZoneId Nullable. Default is {@link java.util.TimeZone#getDefault()}.
     * @param skeleton Nonnull.
     * @return Nonnull.
     */
    public static String date(Locale locale, String timeZoneId, long timeMillis, String skeleton) {
        Preconditions.checkNotNull(skeleton);

        if (locale == null) {
            locale = Locale.getDefault();
        }

        if (timeMillis < 0) {
            return text(locale, null, NOT_AVAILABLE_KEY);
        }

        if (timeZoneId == null) {
            timeZoneId = java.util.TimeZone.getDefault().getID();
        }

        Date time = new Date(timeMillis);

        if (DATE_AND_TIME_SKELETON.equals(skeleton)) {
            skeleton = dateSkeleton(timeMillis) + " " + ICU_TIME_SKELETON;

        } else if (DATE_ONLY_SKELETON.equals(skeleton)) {
            skeleton = dateSkeleton(timeMillis);

        } else if (TIME_ONLY_SKELETON.equals(skeleton)) {
            skeleton = ICU_TIME_SKELETON;
        }

        DateFormat format = DateFormat.getPatternInstance(skeleton, locale);
        format.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        return format.format(new Date(timeMillis));
    }

    private static String dateSkeleton(long timeMillis) {
        return Instant.now().get(ChronoField.YEAR) == Instant.ofEpochMilli(timeMillis).get(ChronoField.YEAR)
                ? ICU_DATE_SKELETON
                : ICU_DATE_WITH_YEAR_SKELETON;
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
