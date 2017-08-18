package com.ap.database.utils.pojo2dll;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ap.database.utils.pojo2dll.api.field.FieldDescriptor;
import com.ap.database.utils.pojo2dll.api.table.Relationship;
import com.ap.database.utils.pojo2dll.api.table.TableDescription;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static java.lang.Class.forName;
import static org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors;
import static org.apache.commons.lang3.StringUtils.lastIndexOf;
import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.right;
import static org.apache.commons.lang3.reflect.FieldUtils.getDeclaredField;
import static org.springframework.util.ObjectUtils.isEmpty;
import static com.ap.database.utils.pojo2dll.api.Utils.toLowerCamel;
import static com.ap.database.utils.pojo2dll.api.Utils.toUpperCamel;
import static com.ap.database.utils.pojo2dll.api.Utils.toUpperUnderscore;
import static com.ap.database.utils.pojo2dll.api.table.Relationship.Type.MANY_TO_MANY;
import static com.ap.database.utils.pojo2dll.api.table.Relationship.Type.MANY_TO_ONE;
import static com.ap.database.utils.pojo2dll.api.table.Relationship.Type.ONE_TO_MANY;
import static com.ap.database.utils.pojo2dll.api.table.Relationship.Type.ONE_TO_ONE;

@Service
public class Pojo2Ddl {
    private static final Logger logger = LoggerFactory.getLogger(Pojo2Ddl.class);

    private static final Map<String, TableDescription> POJO_MAP = new HashMap<>();
    private static final Map<String, TableDescription> JOIN_MAP = new HashMap<>();
    private static final ArrayList<Map.Entry<Class, FieldDescriptor>> ONE_TO_MANES = new ArrayList<>();
    private static final List<String> ALTERING_STATEMENTS = new ArrayList<>();

    // допущение основано на том что классы сущностей должны иметь разные "короткие(простые)" имена,
    // что вообще говоря правильно (хотя и не обязательно в общем случае),
    // в основе названия таблиц так же лежит короткое имя класса
    private final HashSet<String> pojoSimpleNames;

    public Pojo2Ddl(String[] pojoNames) {

        if (pojoNames == null) {
            this.pojoSimpleNames = new HashSet<>();
            return;
        } else {
            //список нам нужен построения отношений в один проход и простая проверка на дублирование имен
            this.pojoSimpleNames = new HashSet<>(asSimpleNames(pojoNames));
        }

        for (String pojoName : pojoNames) {
            try {
                Class<?> aClass = forName(pojoName);
                POJO_MAP.put(asSimpleName(pojoName), new TableDescription(aClass, buildFieldDescriptors(aClass)));
            } catch (ClassNotFoundException e) {
                logger.warn("ClassNotFoundException for {} - ignored", pojoName);
            }
        }

        for (Map.Entry<Class, FieldDescriptor> entry : ONE_TO_MANES) {
            TableDescription tableDescription = POJO_MAP.get(entry.getValue().getRelationship().getName());
            final String name = toLowerCamel(entry.getKey().getSimpleName());
            FieldDescriptor fieldDescriptor = tableDescription.getFieldDescriptors().get(name + "Id");
            if (fieldDescriptor == null) {
                throw new IllegalStateException("Pojo Error: ManyToOne field nod defined");
            } else {
                fieldDescriptor.getRelationship().setType(MANY_TO_ONE).setName(name);
            }
        }
    }

    private Collection<String> asSimpleNames(String... pojoNames) {
        Collection<String> collection = new ArrayList<>();
        for (String pojoName : pojoNames) {
            collection.add(asSimpleName(pojoName));
        }
        return collection;
    }

    private String asSimpleName(String pojoName) {
        return right(pojoName, pojoName.length() - lastIndexOf(pojoName, ".") - 1);
    }

    public Collection<TableDescription> getPojoTables() {
        return POJO_MAP.values();
    }

    public Map<String, FieldDescriptor> buildFieldDescriptors(Class<?> aClass) {

        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(aClass);
        Map<String, FieldDescriptor> fieldDescriptors = new HashMap<>();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

            Field field = getDeclaredField(aClass, propertyDescriptor.getName(), true);

            if (!isEmpty(field)) {
                FieldDescriptor fieldDescriptor = buildFieldDescriptor(aClass, field);
                if (!isEmpty(fieldDescriptor)) {
                    fieldDescriptors.put(propertyDescriptor.getName(), fieldDescriptor);
                }
            }
        }
        return fieldDescriptors;
    }

    private FieldDescriptor buildFieldDescriptor(Class<?> aClass, Field field) {

        Class<?> type = field.getType();
        String name = field.getName();

        Relationship relationship = new Relationship();
        boolean primaryKey = false;

        if (List.class.isAssignableFrom(type)) {
            ParameterizedType listType = (ParameterizedType) field.getGenericType();
            Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];

            String listName = conventionListName(name);

            if (pojoSimpleNames.contains(listClass.getSimpleName())) {
                // TODO это конструкция как JPA лист объектов - связных сущностей
            } else if (Long.class.isAssignableFrom(listClass) && pojoSimpleNames.contains(listName)) {
                // TODO но мы обычно используем список ID объектов - связных сущностей и называем classNameIds

                if (POJO_MAP.containsKey(listName)) { // если мы уже прошли по этому pojo то проверим взимоотношение

                    String simpleName = aClass.getSimpleName();
                    TableDescription tableDescription = POJO_MAP.get(listName);
                    FieldDescriptor fieldDescriptor = tableDescription.findOneToMany(simpleName);
                    // если оно OneToMany, то у обоих нужно поменять на ManyToMany для соответствующих полей
                    if (fieldDescriptor != null) {
                        relationship.setType(MANY_TO_MANY).setName(listName);
                        Relationship oneToManyRelationship = fieldDescriptor.getRelationship();
                        oneToManyRelationship.setType(MANY_TO_MANY).setName(simpleName);
                        ONE_TO_MANES.remove(fieldPointer(tableDescription.getPojoClass(), fieldDescriptor));
                    }

                } else {//если нет то пока мы можем сказать то это как минимум one-to-many
                    relationship = new Relationship(ONE_TO_MANY, listName);
                }
            }
        } else if (Long.class.isAssignableFrom(type) && name.equals("id")) {
            primaryKey = true;
        } else if (Long.class.isAssignableFrom(type) && name.endsWith("Id")) {
            //считаем что на Id заканчивается foreign key
            final String conventionName = conventionName(name);
            if(!pojoSimpleNames.contains(conventionName)){
                throw new IllegalStateException("Name convention " + name);
            }
            relationship = new Relationship(ONE_TO_ONE, conventionName);
        }
        // билдер нужен !!!
        FieldDescriptor fieldDescriptor = new FieldDescriptor(name, type, relationship, primaryKey);

        if (relationship.getType() == ONE_TO_MANY) {
            ONE_TO_MANES.add(fieldPointer(aClass, fieldDescriptor));
        }

        return fieldDescriptor;
    }

    @NotNull
    private AbstractMap.SimpleImmutableEntry<Class, FieldDescriptor> fieldPointer(Class<?> aClass, FieldDescriptor fieldDescriptor) {
        return new AbstractMap.SimpleImmutableEntry<Class, FieldDescriptor>(aClass, fieldDescriptor);
    }

    private String conventionListName(String name) {
        int idx = lastIndexOf(name, "Ids");
        return toUpperCamel(left(name, idx));
    }

    private String conventionName(String name) {
        int idx = lastIndexOf(name, "Id");
        return toUpperCamel(left(name, idx));
    }

    public String mapType(Class<?> type) {
        if (Integer.class.isAssignableFrom(type) || type.equals(Integer.TYPE)) {
            return "INT";
        } else if (Enum.class.isAssignableFrom(type)) {
            return "INT";
//            return "ENUM";
        } else if (String.class.isAssignableFrom(type)) {
            return "VARCHAR2(255)";
        } else if (Boolean.class.isAssignableFrom(type) || type.equals(Boolean.TYPE)) {
            return "BOOLEAN";
        } else if (Byte.class.isAssignableFrom(type) || type.equals(Byte.TYPE)) {
            return "TINYINT";
        } else if (Short.class.isAssignableFrom(type) || type.equals(Short.TYPE)) {
            return "SMALLINT";
        } else if (Long.class.isAssignableFrom(type) || type.equals(Long.TYPE)) {
            return "BIGINT";
        } else if (Date.class.isAssignableFrom(type)) {
            return "TIMESTAMP";
        }
        // TODO belongs schema
        return "OTHER";
    }

    public String asDbName(String name, Class<?> type) {
        if (Enum.class.isAssignableFrom(type)) {
            return toUpperUnderscore(type.getSimpleName());
        } else {
            return toUpperUnderscore(name);
        }
    }

    public void addJoinTableIfNotExist(TableDescription tableDescription) {
        JOIN_MAP.put(tableDescription.getJoinTable().generateName(), tableDescription);
    }

    public Collection<TableDescription> getTables() {
        return JOIN_MAP.values();
    }

    public void addAlteringStatement(String sql) {
        ALTERING_STATEMENTS.add(sql);
    }

    public static List<String> getAlteringStatements() {
        return ALTERING_STATEMENTS;
    }
}
