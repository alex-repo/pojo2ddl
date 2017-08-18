package com.ap.database.utils.pojo2dll.api.table;

import org.jetbrains.annotations.NotNull;
import com.ap.database.utils.pojo2dll.api.field.FieldDescriptor;

import static com.ap.database.utils.pojo2dll.api.Utils.buildJoinTableFieldDescriptors;
import static com.ap.database.utils.pojo2dll.api.Utils.toUpperUnderscore;

public final class JoinTable {
    private final String pojoName1;
    private final String pojoName2;

    //  pojo names mustn't be null
    JoinTable(@NotNull String pojoName1, @NotNull String pojoName2) {
        // не могут быть равны
        // по логике не важно какой порядок строк,
        // поэтому чтоб упростить сравнение и правильно считать hash отсортируем
        if (pojoName1.compareTo(pojoName2) < 0) {
            this.pojoName1 = pojoName1;
            this.pojoName2 = pojoName2;
        } else {
            this.pojoName2 = pojoName1;
            this.pojoName1 = pojoName2;
        }
    }

    String getName() {
        return toUpperUnderscore(pojoName1 + "_" + pojoName2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinTable joinTable = (JoinTable) o;
        return pojoName1.equals(joinTable.pojoName1) && pojoName2.equals(joinTable.pojoName2);
    }

    @Override
    public int hashCode() {
        int result = pojoName1.hashCode();
        result = 31 * result + pojoName2.hashCode();
        return result;
    }

    @NotNull
    public String generateName() {
        return "JT" + hashCode();
    }

    public static TableDescription buildJoinTableDescription(String tableName, FieldDescriptor fieldDescriptor) {
        String otherTableName = fieldDescriptor.getRelationship().getName();
        return new TableDescription(
                JoinTable.class,
                buildJoinTableFieldDescriptors(tableName, otherTableName),
                new JoinTable(tableName, otherTableName)
        );
    }
}
