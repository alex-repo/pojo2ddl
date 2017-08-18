package com.ap.database.utils.pojo2dll.api.schema;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.stereotype.Service;
import com.ap.database.utils.pojo2dll.api.table.TableDescription;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;

@Service
public interface DbService {
    DataSource build(EmbeddedDatabaseType type);

    int insertIntoUniversal(TableDescription tableDescription, Long id, String json);

    void drop(Collection<TableDescription> tables);

    int selectCount(TableDescription table);

    void createUniversalDB(Collection<TableDescription> tables);

    void createDB(Collection<TableDescription> tables);

    void backup();

    Collection<TableDescription> getTables();

    int insertInto(TableDescription tableDescription, Map<String, Object> objectMap);
}
