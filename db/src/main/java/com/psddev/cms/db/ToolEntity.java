package com.psddev.cms.db;

import com.psddev.dari.db.Recordable;
import com.psddev.dari.db.State;

public interface ToolEntity extends Recordable {

    /**
     * Returns all tool users that are represented by this entity.
     *
     * @return Never {@code null}.
     */
    public Iterable<? extends ToolUser> getUsers();

    /**
     * Returns the permission id for this entity.
     *
     * @return Nonnull.
     */
    default String getPermissionId() {
        return "entity/" + State.getInstance(this).getId();
    }
}
