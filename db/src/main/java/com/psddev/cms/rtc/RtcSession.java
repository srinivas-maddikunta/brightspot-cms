package com.psddev.cms.rtc;

import com.psddev.dari.db.Database;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;

import java.util.List;
import java.util.UUID;

/**
 * @since 3.1
 */
public class RtcSession extends Record {

    private UUID userId;

    @Indexed
    private long lastPing;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public long getLastPing() {
        return lastPing;
    }

    public void setLastPing(long lastPing) {
        this.lastPing = lastPing;
    }

    public void disconnect() {
        List<RtcEvent> events = Query
                .from(RtcEvent.class)
                .where("cms.rtc.event.sessionId = ?", getId())
                .selectAll();

        Database database = Database.Static.getDefault();

        database.beginWrites();

        try {
            events.forEach(RtcEvent::onDisconnect);
            database.commitWrites();

        } finally {
            database.endWrites();
        }

        database.beginWrites();

        try {
            delete();
            events.forEach(e -> e.getState().delete());
            database.commitWrites();

        } finally {
            database.endWrites();
        }
    }
}
