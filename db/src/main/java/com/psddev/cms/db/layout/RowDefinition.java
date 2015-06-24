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
import com.psddev.dari.db.Record;
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

    private LayoutNode createRootNode(ObjectType type) {

        LayoutNode rootNode = createLayoutNode(type);

        if (rootNode == null) {
            return null;
        }

        LayoutNode.setAllLayoutAttributesFromRoot(rootNode);

        return rootNode;
    }

    public LayoutNode createLayoutNode(ObjectType type) {

        List<ColumnDefinition> columnDefinitions = this.getColumnDefinitions();

        LayoutNode.ContainerNode containerNode =  new LayoutNode.ContainerNode();
        containerNode.setContainerType(LayoutNode.ContainerNode.ContainerType.ROW);

        List<LayoutNode> childNodes = new ArrayList<>();
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            LayoutNode columnNode = columnDefinition.createLayoutNodes(type);
            if (columnNode != null) {
                columnNode.setParent(containerNode);
                childNodes.add(columnNode);
            }
        }

        containerNode.setChildNodes(childNodes);

        return containerNode;
    }

    private List<ObjectField> createFields(ObjectType type) {

        LayoutNode rootNode = createRootNode(type);

        List<ObjectField> fields = new ArrayList<>();
        for (LayoutNode.FieldNode fieldNode : LayoutNode.getAllFieldNodes(rootNode)) {
            ObjectField realField = new ObjectField(type, fieldNode.getFieldWithToolUiLayoutElement().toDefinition());
            realField.setInternalType(ObjectField.RECORD_TYPE);

            //TODO: add type restrictions to Column$Cell
            realField.getTypes().add(ObjectType.getInstance(Record.class));
            fields.add(realField);
        }

        return fields;
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

        if (ObjectUtils.firstNonNull((Boolean) this.getState().getExtra("isEmbedded"), false)) {
            return;
        }

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
        ObjectType rowType = ObjectType.getInstance(Row.class);
        List<ObjectField> fields = createFields(newRowType);

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
