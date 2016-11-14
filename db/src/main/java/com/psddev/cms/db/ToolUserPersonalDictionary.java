package com.psddev.cms.db;

import com.psddev.dari.db.Record;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ToolUserPersonalDictionary extends Record {

    private Set<String> words;

    @Indexed
    private UUID userId;

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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void add(String word) {
        if (words == null) {
            words = new HashSet<>();
        }
        words.add(word);
    }
}