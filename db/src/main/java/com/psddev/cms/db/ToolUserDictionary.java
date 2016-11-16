package com.psddev.cms.db;

import com.psddev.cms.nlp.SpellChecker;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class ToolUserDictionary extends Record {

    private Set<String> words;

    @Required
    @ToolUi.ReadOnly
    @Indexed
    private UUID userId;

    @Required
    @ToolUi.ReadOnly
    @Indexed
    private String languageTag;

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
        return "Custom " + Locale.forLanguageTag(languageTag).getDisplayLanguage() + " Dictionary";
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getLocaleLanguageCode() {
        return languageTag;
    }

    public void setLocaleLanguageCode(String localeLanguageCode) {
        this.languageTag = localeLanguageCode;
    }

    public void add(String word) {
        if (words == null) {
            words = new HashSet<>();
        }
        words.add(word);
    }

    @Override
    protected void beforeSave() {
        Locale languageTag = Locale.forLanguageTag(getLocaleLanguageCode());
        Locale locale = new Locale(languageTag.getLanguage(), languageTag.getCountry(), userId.toString());

        ToolUserDictionary userDictionary = Query.from(ToolUserDictionary.class).where("id = ?", getId()).first();

        if (userDictionary != null) {
            SpellChecker spellChecker = SpellChecker.getInstance(locale);

            if (spellChecker != null) {
                Set<String> previousWords = Query.from(ToolUserDictionary.class).where("id = ?", getId()).first().getWords();


                if (getWords().size() < previousWords.size()) {
                    // one or more words were removed
                    previousWords.removeAll(words);
                    for (String word : previousWords) {
                        spellChecker.remove(locale, word);
                    }

                } else if (getWords().size() > previousWords.size()) {

                    Set<String> newWords = new HashSet<>(getWords());
                    newWords.removeAll(previousWords);

                    for (String word : newWords) {
                        spellChecker.add(locale, word, false);
                    }
                }
            }
        }
    }

    @Override
    protected void beforeDelete() {

        Locale languageTag = Locale.forLanguageTag(getLocaleLanguageCode());
        Locale locale = new Locale(languageTag.getLanguage(), languageTag.getCountry(), userId.toString());
        SpellChecker spellChecker = SpellChecker.getInstance(locale);

        if (spellChecker != null) {
            for (String word : getWords()) {
                spellChecker.remove(locale, word);
            }
        }
    }
}
