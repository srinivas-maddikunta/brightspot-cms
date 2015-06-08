package com.psddev.cms.db.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.psddev.cms.db.Content;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Database;
import com.psddev.dari.db.DatabaseEnvironment;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.State;
import com.psddev.dari.util.ObjectUtils;

/**
 * Allows creation of editorially defined
 * {@link Row} ObjectTypes.
 */
public class RowDefinition extends Content {

    @Indexed(unique = true)
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
        if (columnDefinitions == null) {
            columnDefinitions = new ArrayList<>();
        }
        return columnDefinitions;
    }

    public void setColumnDefinitions(List<ColumnDefinition> columnDefinitions) {
        this.columnDefinitions = columnDefinitions;
    }

    public String createInstanceTypeName() {
        return "com.psddev.cms.db.layout.row." + getName();
    }

    public List<ObjectField> createFields(ObjectType type, int topOffset, int leftOffset) {
        List<ColumnDefinition> columnDefinitions = this.getColumnDefinitions();

        List<ObjectField> newFields = new ArrayList<>();
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            List<ObjectField> columnFields = columnDefinition.createFields(type, topOffset, leftOffset);

            if (!ObjectUtils.isBlank(columnFields)) {
                newFields.addAll(columnFields);
            }

            leftOffset += columnDefinition.getWidth();
        }

        return newFields;
    }

    /**
     * Generates new {@link Row} ObjectType, based on
     * the {@link RowDefinition}. Fields for each column
     * will be created. The Field UI is defined by calculating
     * values to create a {@link com.psddev.cms.db.ToolUiLayoutElement}
     * for each field.
     */
    @Override
    public void afterSave() {
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

        int topOffset = 0;
        int leftOffset = 0;
        for (ColumnDefinition columnDefinition : columnDefinitions) {

            List<ObjectField> createdFields = columnDefinition.createFields(newRowType, topOffset, leftOffset);
            if (!ObjectUtils.isBlank(createdFields)) {
                fields.addAll(createdFields);
            }

            leftOffset += columnDefinition.getWidth();
        }

        State typeState = newRowType.getState();

        typeState.putAll(rowType.getState().getSimpleValues());
        typeState.setId(typeId);

        newRowType.setAbstract(false);
        newRowType.setEmbedded(true);
        newRowType.setAssignableClassNames(null);
        newRowType.setGroups(null);
        newRowType.setIndexes(null);
        newRowType.setJavaBeanProperty(null);
        newRowType.setModificationClasses(null);
        newRowType.setObjectClassName(null);
        newRowType.setSuperClassNames(null);
        newRowType.as(ToolUi.class).setNoteHtml(null);

        newRowType.setDisplayName(getName());
        newRowType.setFields(fields);
        newRowType.setInternalName(typeName);

        newRowType.setGroups(rowType.getGroups());
        newRowType.saveImmediately();
        environment.refreshTypes();
        environment.refreshTypes();
    }
}
