package com.psddev.cms.rtc;

import com.psddev.dari.db.Database;
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
        RtcSessionTaskStatus status = Query.from(RtcSessionTaskStatus.class).first();
        long currentTimeMillis = currentTime.getMillis();

        if (status.getLastRun() < currentTimeMillis) {
            status.getState().replaceAtomically("lastRun", currentTimeMillis);

            try {
                status.save();

            } catch (DatabaseException error) {
                return;
            }
        }

        Query<RtcSession> query = Query
                .from(RtcSession.class)
                .where("lastPing < ?", Database.Static.getDefault().now() - 60L * 1000L);

        for (List<RtcSession> sessions; !(sessions = query.select(0, 100).getItems()).isEmpty();) {
            sessions.forEach(RtcSession::disconnect);
        }
    }
}
