package com.ap.database.utils.pojo2dll.api;

import com.ap.database.utils.pojo2dll.api.field.FieldDescriptor;
import com.ap.database.utils.pojo2dll.api.table.Relationship;
import com.ap.database.utils.pojo2dll.api.table.Table;
import com.ap.database.utils.pojo2dll.api.table.TableDescription;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.ap.database.utils.pojo2dll.api.table.Relationship.Type.MANY_TO_ONE;

public class Utils {

    private static final AtomicLong ID = new AtomicLong(0L);

    private static Long nextId() {
        return ID.getAndIncrement();
    }

    // должны быть настройки на соглашение
    public static String toUpperUnderscore(String name) {
        return LOWER_CAMEL.to(UPPER_UNDERSCORE, name);
    }

    // должны быть настройки на соглашение
    public static String toUpperCamel(String name) {
        return LOWER_CAMEL.to(UPPER_CAMEL, name);
    }

    public static String toLowerCamel(String name) {
        return UPPER_CAMEL.to(LOWER_CAMEL, name);
    }

    // используется в тестах
    public static Table generate(TableDescription table) throws IllegalAccessException, InstantiationException {
        final Table tmpTable = new Table(table, table.getPojoClass().newInstance());
        final FieldDescriptor fieldDescriptor = tmpTable.getTableDescription().findPrimaryKey();
        if(fieldDescriptor != null){
            tmpTable.setField(0, fieldDescriptor, nextId());
        }
        return tmpTable;
    }

    public static Map<String, FieldDescriptor> buildJoinTableFieldDescriptors(String tableName, String otherTableName) {
        Map<String, FieldDescriptor> fieldDescriptors = new HashMap<>();
        Relationship relationship = new Relationship().setType(MANY_TO_ONE).setName(tableName);
        fieldDescriptors.put(tableName + "Id", new FieldDescriptor(tableName + "Id", Long.class, relationship));
        Relationship relationship1 = new Relationship().setType(MANY_TO_ONE).setName(tableName);
        fieldDescriptors.put(otherTableName + "Id", new FieldDescriptor(otherTableName + "Id", Long.class, relationship1));
        return fieldDescriptors;
    }
}
