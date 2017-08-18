package com.ap.database.utils.pojo2dll.api.table;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class Relationship {

    private Type type;
    private String name;

    public enum Type {NONE, ONE_TO_MANY, MANY_TO_MANY, MANY_TO_ONE, ONE_TO_ONE}

    public Relationship(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public Relationship() {
        type = Type.NONE;

        name = EMPTY;
    }

    public Type getType() {
        return type;
    }

    public Relationship setType(Type type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public Relationship setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relationship that = (Relationship) o;

        if (getType() != that.getType()) return false;
        return getName() != null ? getName().equals(that.getName()) : that.getName() == null;
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}
