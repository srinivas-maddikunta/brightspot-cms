package com.psddev.cms.db;

import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("All")
public class AllSiteGroup extends SiteGroup {

    @Override
    public boolean contains(Site site) {
        return true;
    }

    @Override
    public String getLabel() {
        return "All Sites";
    }
}
