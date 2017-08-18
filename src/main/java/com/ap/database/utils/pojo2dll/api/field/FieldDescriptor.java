package com.ap.database.utils.pojo2dll.api.field;

import com.ap.database.utils.pojo2dll.api.table.Relationship;

import static com.ap.database.utils.pojo2dll.api.table.Relationship.Type.MANY_TO_MANY;

public class FieldDescriptor {
    private final String javaName;
    private final Class javaType;
    private Relationship relationship;
    private boolean primaryKey = false;
    private boolean nullable = true;

    public FieldDescriptor(String javaName, Class javaType, Relationship relationship, boolean primaryKey) {
        this.javaName = javaName;
        this.javaType = javaType;
        this.relationship = relationship;
        this.primaryKey = primaryKey;
    }

    public FieldDescriptor(String javaName, Class javaType, Relationship relationship) {
        this.javaName = javaName;
        this.javaType = javaType;
        this.relationship = relationship;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public String getJavaName() {
        return javaName;
    }

    public Class getJavaType() {
        return javaType;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public boolean isManyToMany() {
        return relationship.getType() == MANY_TO_MANY;
    }
}
