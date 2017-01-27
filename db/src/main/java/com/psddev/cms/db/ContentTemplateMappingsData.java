package com.psddev.cms.db;

import com.psddev.cms.tool.CmsTool;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.Recordable;

import java.util.LinkedHashSet;
import java.util.Set;

@Recordable.FieldInternalNamePrefix("cms.contentTemplate.")
@Modification.Classes({ CmsTool.class, ToolEntity.class })
public class ContentTemplateMappingsData extends Modification<Object> {

    @ToolUi.Tab("Content Templates")
    private Set<ContentTemplateMapping> mappings;

    public Set<ContentTemplateMapping> getMappings() {
        if (mappings == null) {
            mappings = new LinkedHashSet<>();
        }
        return mappings;
    }

    public void setMappings(Set<ContentTemplateMapping> mappings) {
        this.mappings = mappings;
    }
}
