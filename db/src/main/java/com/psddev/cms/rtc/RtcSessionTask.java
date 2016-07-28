package com.psddev.cms.rtc;

import com.psddev.dari.db.DatabaseException;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.RepeatingTask;
import org.joda.time.DateTime;

import java.util.List;

public class RtcSessionTask extends RepeatingTask {

    @Override
    protected DateTime calculateRunTime(DateTime currentTime) {
        return everyMinute(currentTime);
    }

    @Override
    protected void doRepeatingTask(DateTime currentTime) throws Exception {
        long now = System.currentTimeMillis();
        long past = now - 60000;
        RtcSessionTaskStatus status = Query.from(RtcSessionTaskStatus.class).first();

        if (status.getLastRun() < past) {
            status.getState().replaceAtomically("lastRun", now);

            try {
                status.save();

            } catch (DatabaseException error) {
                return;
            }
        }

        Query<RtcSession> query = Query
                .from(RtcSession.class)
                .where("lastPing < ?", past);

        for (List<RtcSession> sessions; !(sessions = query.select(0, 100).getItems()).isEmpty();) {
            sessions.forEach(RtcSession::disconnect);
        }
    }
}
