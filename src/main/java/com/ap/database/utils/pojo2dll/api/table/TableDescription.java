package com.ap.database.utils.pojo2dll.api.table;

import com.ap.database.utils.pojo2dll.api.field.FieldDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ap.database.utils.pojo2dll.api.Utils.toUpperUnderscore;
import static com.ap.database.utils.pojo2dll.api.table.Relationship.Type.ONE_TO_MANY;

public class TableDescription {
    private final Class<?> aClass;
    private Map<String, FieldDescriptor> fieldDescriptors;
    private JoinTable joinTable;

    public TableDescription(Class<?> aClass, Map<String, FieldDescriptor> fieldDescriptors) {
        this.aClass = aClass;
        this.fieldDescriptors = fieldDescriptors;
    }

    TableDescription(Class<?> aClass, Map<String, FieldDescriptor> fieldDescriptors, JoinTable joinTable) {
        this.aClass = aClass;
        this.fieldDescriptors = fieldDescriptors;
        this.joinTable = joinTable;
    }

    public String getName() {
        if (joinTable != null) {
            return joinTable.getName();
        } else {
            return toUpperUnderscore(aClass.getSimpleName());
        }
    }

    public Class<?> getPojoClass() {
        return aClass;
    }

    public Map<String, FieldDescriptor> getFieldDescriptors() {
        return fieldDescriptors;
    }

    public FieldDescriptor findOneToMany(String simpleName) {
        for (Map.Entry<String, FieldDescriptor> entry : getFieldDescriptors().entrySet()) {
            FieldDescriptor fieldDescriptor = entry.getValue();
            if (fieldDescriptor.getRelationship().equals(new Relationship(ONE_TO_MANY, simpleName))) {
                return fieldDescriptor;
            }
        }
        return null;
    }

    public List<String> findManyToMany() {
        List<String> fieldDescriptorsName = new ArrayList<>();
        for (Map.Entry<String, FieldDescriptor> entry : getFieldDescriptors().entrySet()) {
            if (entry.getValue().isManyToMany()) {
                fieldDescriptorsName.add(entry.getKey());
            }
        }
        return fieldDescriptorsName;
    }

    public JoinTable getJoinTable() {
        if (joinTable == null) {
            throw new IllegalStateException("Couldn't create join table for " + getName());
        }
        return joinTable;
    }

    public FieldDescriptor findPrimaryKey() {
        for (Map.Entry<String, FieldDescriptor> entry : fieldDescriptors.entrySet()) {
            if (entry.getValue().isPrimaryKey()) {
                return entry.getValue();
            }
        }
        return null;
    }

    public Class<?> getType(String name) {
        FieldDescriptor fieldDescriptor = getFieldDescriptors().get(name);
        return fieldDescriptor == null ? null : fieldDescriptor.getJavaType();
    }
}
