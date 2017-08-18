import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.ap.database.utils.pojo2dll.api.Utils;
import com.ap.database.utils.pojo2dll.api.schema.DbService;
import com.ap.database.utils.pojo2dll.api.table.Table;
import com.ap.database.utils.pojo2dll.api.table.TableDescription;
import com.ap.database.utils.pojo2dll.utils.JacksonMarshaller;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class DataServiceLayerTest {

    @Autowired
    private DbService dbService;
    @Autowired
    private JacksonMarshaller marshaller;

    @Test
    public void createUniversalTables() throws Exception {
        Collection<TableDescription> tables = dbService.getTables();
        dbService.createUniversalDB(tables);
        insertOneUniversalRecord(tables);
        dbService.drop(tables);
    }

    @Test
    public void createTables() throws Exception {
        Collection<TableDescription> tables = dbService.getTables();
        dbService.createDB(tables);
        dbService.backup();
        insertOneRecord(tables);
        dbService.drop(tables);
    }

    private void insertOneRecord(Collection<TableDescription> tables) throws InstantiationException, IllegalAccessException {
        for (TableDescription tableDescription : tables) {

            final Table table = Utils.generate(tableDescription);
            Map<String, Object> objectMap = marshaller.toMap(table.getRow(0));
            List<String> fields = tableDescription.findManyToMany();
            for (String name : fields) {
                objectMap.remove(name);
            }

            int rowCount = dbService.insertInto(tableDescription, objectMap);
            assertEquals(rowCount, 1);
            rowCount = dbService.selectCount(tableDescription);
            assertEquals(rowCount, 1);
        }
    }

    private void insertOneUniversalRecord(Collection<TableDescription> tables) throws IllegalAccessException, InstantiationException {
        for (TableDescription tableDescription : tables) {

            final Table table = Utils.generate(tableDescription);
            final Object row = table.getRow(0);
            Long id = (Long) readField(row, "id");
            String json = marshaller.toJson(row);

            int rowCount = dbService.insertIntoUniversal(tableDescription, id,json);
            assertEquals(rowCount, 1);
            rowCount = dbService.selectCount(tableDescription);
            assertEquals(rowCount, 1);
        }
    }
}