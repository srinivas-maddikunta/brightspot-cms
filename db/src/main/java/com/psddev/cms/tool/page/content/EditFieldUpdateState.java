package com.psddev.cms.tool.page.content;

import com.psddev.cms.rtc.RtcState;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.ObjectUtils;

import java.util.Map;
import java.util.UUID;

class EditFieldUpdateState implements RtcState {

    @Override
    public Iterable<?> create(Map<String, Object> data) {
        return Query.from(EditFieldUpdate.class)
                .where("contentId = ?", data.get("contentId"))
                .selectAll();
    }

    @Override
    public Iterable<?> close(Map<String, Object> data, UUID userId) {
        return Query
                .from(EditFieldUpdate.class)
                .where("_id = ?", EditFieldUpdate.id(userId, ObjectUtils.to(UUID.class, data.get("contentId"))))
                .selectAll();
    }
}
