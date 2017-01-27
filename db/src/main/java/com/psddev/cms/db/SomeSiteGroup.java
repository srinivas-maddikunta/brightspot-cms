package com.psddev.cms.db;

import com.psddev.dari.db.Recordable;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Recordable.DisplayName("Some")
public class SomeSiteGroup extends SiteGroup {

    @Required
    @ToolUi.Unlabeled
    private Set<Site> sites;

    public Set<Site> getSites() {
        if (sites == null) {
            sites = new LinkedHashSet<>();
        }
        return sites;
    }

    public void setSites(Set<Site> sites) {
        this.sites = sites;
    }

    @Override
    public boolean contains(Site site) {
        return getSites().contains(site);
    }

    @Override
    public String getLabel() {
        return getSites().stream()
                .map(Site::getLabel)
                .collect(Collectors.joining(", "));
    }
}
