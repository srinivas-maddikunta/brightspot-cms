package com.psddev.cms.db;

import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@Recordable.Embedded
public abstract class SiteGroup extends Record {

    public abstract boolean contains(Site site);
}
