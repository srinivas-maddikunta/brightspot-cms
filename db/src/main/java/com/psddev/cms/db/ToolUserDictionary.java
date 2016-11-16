package com.psddev.cms.db;

import com.psddev.cms.nlp.SpellChecker;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class ToolUserDictionary extends Record {

    private Set<String> words;

    @ToolUi.ReadOnly
    @Recordable.Indexed
    private UUID userId;

    @ToolUi.ReadOnly
    @Recordable.Indexed
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

    @Override
    protected void beforeCommit() {
        Locale languageTag = Locale.forLanguageTag(localeLanguageCode);
        Locale locale = new Locale(languageTag.getLanguage(), languageTag.getCountry(), userId.toString());
        SpellChecker spellChecker = SpellChecker.getInstance(locale);

        Set<String> previousWords = Query.from(ToolUserDictionary.class).where("id = ?", getId()).first().getWords();
        if (words.size() < previousWords.size()) {
            // one or more words were removed
            previousWords.removeAll(words);
            for (String word : previousWords) {
                spellChecker.remove(locale, word);
            }

        } else if (words.size() > previousWords.size()) {

            Set<String> newWords = new HashSet<>(words);
            newWords.removeAll(previousWords);

            for (String word : newWords) {
                spellChecker.add(locale, word, false);
            }
        }
    }
}
