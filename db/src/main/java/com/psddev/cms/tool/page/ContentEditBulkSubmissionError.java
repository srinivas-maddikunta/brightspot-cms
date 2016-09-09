package com.psddev.cms.tool.page;

import com.psddev.dari.db.Record;

public class ContentEditBulkSubmissionError extends Record {

    private String errorLabel;

    private String stackTrace;

    public String getErrorLabel() {
        return errorLabel;
    }

    public void setErrorLabel(String errorLabel) {
        this.errorLabel = errorLabel;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
