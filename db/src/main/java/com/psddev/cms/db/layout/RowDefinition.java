package com.psddev.cms.db.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.psddev.cms.db.Content;
import com.psddev.dari.db.Database;
import com.psddev.dari.db.DatabaseEnvironment;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.State;
import com.psddev.dari.util.ObjectUtils;

public class RowDefinition extends Content {

    @Indexed
    @Required
    private String name;

    @Embedded
    private List<ColumnDefinition> columnDefinitions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ColumnDefinition> getColumnDefinitions() {
        return columnDefinitions;
    }

    public void setColumnDefinitions(List<ColumnDefinition> columnDefinitions) {
        this.columnDefinitions = columnDefinitions;
    }

    public String createInstanceTypeName() {
        return "cms.row" + getId();
    }

    /**
     * Dynamically generates new ObjectType representation of Row instance
     */
    @Override
    public void afterSave() {
        Logger.getAnonymousLogger().info("ROW CREATOR AFTER SAVE");
        DatabaseEnvironment environment = Database.Static.getDefault().getEnvironment();
        ObjectType newRowType = new ObjectType();
        String typeName = createInstanceTypeName();

        //check for existing equivalent type
        for (ObjectType t : environment.getTypes()) {
            if (typeName.equals(t.getInternalName())) {
                newRowType = t;
                break;
            }
        }

        UUID typeId = newRowType.getId();
        List<ObjectField> fields = new ArrayList<>();
        ObjectType rowType = ObjectType.getInstance(Row.class);

        // dynamically creates column fields
        List<ColumnDefinition> columnDefinitions = getColumnDefinitions();
        if (!ObjectUtils.isBlank(columnDefinitions)) {
            int topOffset = 0;
            int leftOffset = 0;
            for (int i = 0; i < columnDefinitions.size(); i++) {

                ColumnDefinition columnDefinition = columnDefinitions.get(i);

                if (columnDefinition == null) {
                    continue;
                }

                ObjectField field = columnDefinition.createColumnField(newRowType,
                        ObjectUtils.firstNonBlank(columnDefinition.getName(), "column" + (i +1)),
                        topOffset,
                        leftOffset);
                leftOffset += columnDefinition.getWidth();
                fields.add(field);
            }
        }

        State typeState = newRowType.getState();

        typeState.putAll(rowType.getState().getSimpleValues());
        typeState.setId(typeId);
        
        newRowType.setAbstract(false);
        newRowType.setEmbedded(true);

        newRowType.setDisplayName(getName());
        newRowType.setFields(fields);
        newRowType.setInternalName(typeName);

        //Set<String> groups = newRowType.getGroups();
        newRowType.setGroups(rowType.getGroups());
        newRowType.saveImmediately();
        environment.refreshTypes();
        environment.refreshTypes();
        Logger.getAnonymousLogger().info("TYPE ID CREATED " + newRowType.getId());
    }
}
