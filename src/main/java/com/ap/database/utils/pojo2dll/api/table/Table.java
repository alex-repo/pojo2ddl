package com.ap.database.utils.pojo2dll.api.table;

import com.ap.database.utils.pojo2dll.api.field.FieldDescriptor;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.reflect.FieldUtils.writeField;

public class Table {

    private TableDescription tableDescription;
    private List<Object> rows = new ArrayList();

    public Table(TableDescription tableDescription, Object pojo) {
        this.tableDescription = tableDescription;
        this.rows.add(pojo);
    }

    public void setField(int idx, FieldDescriptor fieldDescriptor, Object o) {
        try {
            writeField(rows.get(idx), fieldDescriptor.getJavaName(), o, true);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public Object getRow(int idx) {
        return rows.get(idx);
    }

    public TableDescription getTableDescription() {
        return tableDescription;
    }

    public String getName() {
        return getTableDescription().getName();
    }

}