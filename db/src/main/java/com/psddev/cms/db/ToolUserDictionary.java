package com.psddev.cms.db;

import com.psddev.dari.db.Record;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class ToolUserDictionary extends Record {

    private Set<String> words;

    @ToolUi.ReadOnly
    @Indexed
    private UUID userId;

    @ToolUi.ReadOnly
    @Indexed
    private String localeLanguageCode;

    public Set<String> getWords() {
        if (words == null) {
            words = new HashSet<>();
        }
        return words;
    }

    public void setWords(Set<String> words) {
        if (words == null) {
            words = new HashSet<>();
        }
        this.words = words;
    }

    @Override
    public String getLabel() {
        return "Custom " + Locale.forLanguageTag(localeLanguageCode).getDisplayLanguage() + " Dictionary";
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getLocaleLanguageCode() {
        return localeLanguageCode;
    }

    public void setLocaleLanguageCode(String localeLanguageCode) {
        this.localeLanguageCode = localeLanguageCode;
    }

    public void add(String word) {
        if (words == null) {
            words = new HashSet<>();
        }
        words.add(word);
    }
}
