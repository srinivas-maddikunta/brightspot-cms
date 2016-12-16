package com.psddev.cms.rtc;

import com.psddev.dari.db.Record;
import com.psddev.dari.db.Singleton;

class RtcSessionTaskStatus extends Record implements Singleton {

    private long lastRun;

    public long getLastRun() {
        return lastRun;
    }

    public void setLastRun(long lastRun) {
        this.lastRun = lastRun;
    }
}
