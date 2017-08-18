package com.ap.database.utils.pojo2dll.db.h2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.stereotype.Service;
import com.ap.database.utils.pojo2dll.Pojo2Ddl;
import com.ap.database.utils.pojo2dll.api.field.FieldDescriptor;
import com.ap.database.utils.pojo2dll.api.schema.DbService;
import com.ap.database.utils.pojo2dll.api.table.TableDescription;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static com.ap.database.utils.pojo2dll.api.Utils.toUpperUnderscore;
import static com.ap.database.utils.pojo2dll.api.table.JoinTable.buildJoinTableDescription;

@Service
public class H2DbService implements DbService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private Pojo2Ddl pojo2Ddl;
    @Autowired
    Environment environment;
    @Value("${backup.filename}")
    String backupFilename;

    @Override
    public DataSource build(EmbeddedDatabaseType type) {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        EmbeddedDatabase db = builder
                .setType(EmbeddedDatabaseType.H2)
//                .setType(EmbeddedDatabaseType.HSQL)
                /* можно налить скриптами
                .addScript("sql/create-db.sql")
                .addScript("sql/insert-data.sql")*/
                .build();
        return db;
    }

    @Override
    public int insertIntoUniversal(TableDescription tableDescription, Long id, String json) {
        String sql = "INSERT iNTO %s (ID, NAME, DATA, DELETED) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(
                format(sql, tableDescription.getName()),
                id,
                tableDescription.getPojoClass().getName(),
                json,
                0);
    }

    @Override
    public void drop(Collection<TableDescription> tables) {
        String sql = "DROP TABLE %s";// IF EXISTS
        for (TableDescription table : tables) {
            jdbcTemplate.execute(format(sql, table.getName()));
        }
    }

    @Override
    public int selectCount(TableDescription table) {
        String sql = "SELECT COUNT(*) FROM %s";
        return jdbcTemplate.queryForObject(format(sql, table.getName()), Integer.class);
    }

    @Override
    public void createUniversalDB(Collection<TableDescription> tables) {
        for (TableDescription table : tables) {
            createUniversal(table);
        }
    }

    private void createTables(Collection<TableDescription> tables) {
        for (TableDescription table : tables) {
            create(table);
        }
    }

    @Override
    public void createDB(Collection<TableDescription> tables) {
        createTables(tables);
        createTables(pojo2Ddl.getTables());
        execute(pojo2Ddl.getAlteringStatements());
    }

    private void execute(List<String> sqls) {
        for (String sql : sqls) {
            jdbcTemplate.execute(sql);
        }
    }

    // String sql = "INSERT iNTO %s (ID, NAME, DATA, DELETED) VALUES (?, ?, ?, ?)";
    @Override
    public int insertInto(TableDescription tableDescription, Map<String, Object> objectMap) {

        List objects = new ArrayList(objectMap.size());
        StringBuilder sbSqlFields = new StringBuilder("INSERT iNTO ");
        sbSqlFields.append(tableDescription.getName()).append(" (");

        StringBuilder sbSqlValues = new StringBuilder(" VALUES(");

        String prefix = "";
        for (Map.Entry<String, Object> entity : objectMap.entrySet()) {

            Class<?> type = tableDescription.getType(entity.getKey());

            sbSqlFields.append(prefix);
            sbSqlValues.append(prefix);
            prefix = ",";

            sbSqlFields.append(pojo2Ddl.asDbName(entity.getKey(), type));
            sbSqlValues.append("?");
            objects.add(entity.getValue());
        }
        sbSqlFields.append(")");
        sbSqlValues.append(")");
        sbSqlFields.append(sbSqlValues.toString());

        return jdbcTemplate.update(sbSqlFields.toString(), objects.toArray(new Object[objects.size()]));
    }

    @Override
    public void backup() {
        jdbcTemplate.queryForList(" SCRIPT TO ? ", backupFilename);
    }

    @Override
    public Collection<TableDescription> getTables() {
        return pojo2Ddl.getPojoTables();
    }

    private void create(TableDescription table) {
        StringBuilder sbSql = new StringBuilder("CREATE TABLE ");
        final String tableName = table.getName();
        sbSql.append(tableName).append("(");

        StringBuilder sufix = new StringBuilder("");
        String prefix = "";
        Map<String, FieldDescriptor> fieldDescriptors = table.getFieldDescriptors();
        for (Map.Entry<String, FieldDescriptor> entry : fieldDescriptors.entrySet()) {

            FieldDescriptor descriptor = entry.getValue();
            final String fieldDbName = pojo2Ddl.asDbName(descriptor.getJavaName(), descriptor.getJavaType());

            switch (descriptor.getRelationship().getType()) {
                case MANY_TO_MANY:
                    TableDescription tableDescription = buildJoinTableDescription(table.getPojoClass().getSimpleName(), descriptor);
                    pojo2Ddl.addJoinTableIfNotExist(tableDescription);
                    continue;
                case ONE_TO_MANY:
                    break;
                case ONE_TO_ONE:
                case MANY_TO_ONE:
                    pojo2Ddl.addAlteringStatement(
                            new StringBuilder().append("ALTER TABLE ")
                                    .append(tableName)
                                    .append(" ADD FOREIGN KEY (")
                                    .append(fieldDbName)
                                    .append(") REFERENCES ")
                                    .append(toUpperUnderscore(descriptor.getRelationship().getName()))
                                    .append("(ID)")
                                    .toString()
                    );
            }

            sbSql.append(prefix);
            prefix = ",";

            sbSql.append(fieldDbName).append(" ");
            sbSql.append(pojo2Ddl.mapType(descriptor.getJavaType()));
            if (descriptor.isPrimaryKey()) {
                sbSql.append(" PRIMARY KEY");
            }
        }
        sbSql.append(sufix);
        sbSql.append(")");
        jdbcTemplate.execute(sbSql.toString());
    }

    private void createUniversal(TableDescription table) {
        jdbcTemplate.execute(
                "CREATE TABLE " + table.getName() +
                        "(" +
                        "ID NUMBER(19),\n" +
                        "NAME VARCHAR(255),\n" +
                        "DATA VARCHAR (255),\n" +
                        "DELETED INT" +
                        ")\n"
        );
    }
}
