package com.psddev.cms.db;

import com.psddev.dari.db.ForwardingDatabase;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.db.State;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.PaginatedResult;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

class FieldAccessDatabase extends ForwardingDatabase {

    private final Set<UUID> displayIds;

    public FieldAccessDatabase(Set<UUID> displayIds) {
        this.displayIds = displayIds;
    }

    public <T> T addDisplayIds(T value) {
        if (value instanceof Recordable) {
            State valueState = ((Recordable) value).getState();
            UUID id = valueState.getId();

            if (!displayIds.contains(id)) {
                displayIds.add(id);
                addDisplayIds(valueState.getRawValues());
            }

        } else if (value instanceof Iterable) {
            for (Object item : (Iterable<?>) value) {
                addDisplayIds(item);
            }

        } else if (value instanceof Map) {
            Map<?, ?> valueMap = (Map<?, ?>) value;
            UUID id = ObjectUtils.to(UUID.class, valueMap.get("_id"));

            if (id != null) {
                displayIds.add(id);

            } else {
                valueMap.values().forEach(this::addDisplayIds);
            }
        }

        return value;
    }

    @Override
    public <T> List<T> readAll(Query<T> query) {
        return addDisplayIds(super.readAll(query));
    }

    @Override
    public <T> T readFirst(Query<T> query) {
        return addDisplayIds(super.readFirst(query));
    }

    @Override
    public <T> PaginatedResult<T> readPartial(Query<T> query, long offset, int limit) {
        PaginatedResult<T> result = super.readPartial(query, offset, limit);

        addDisplayIds(result.getItems());
        return result;
    }
}
