package com.psddev.cms.db;

import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Recordable.Embedded
public class ContentTemplateMapping extends Record {

    @Required
    private SiteGroup sites = new AllSiteGroup();

    @Required
    private Set<ContentTemplate> contentTemplates;

    public SiteGroup getSites() {
        return sites;
    }

    public void setSites(SiteGroup sites) {
        this.sites = sites;
    }

    public Set<ContentTemplate> getContentTemplates() {
        if (contentTemplates == null) {
            contentTemplates = new LinkedHashSet<>();
        }
        return contentTemplates;
    }

    public void setContentTemplates(Set<ContentTemplate> contentTemplates) {
        this.contentTemplates = contentTemplates;
    }

    @Override
    public String getLabel() {
        return "On " + getSites().getLabel() + ": " + getContentTemplates().stream()
                .map(ContentTemplate::getLabel)
                .collect(Collectors.joining(", "));
    }
}
