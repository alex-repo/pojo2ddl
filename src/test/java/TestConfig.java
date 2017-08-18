import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import com.ap.database.utils.pojo2dll.Pojo2Ddl;
import com.ap.database.utils.pojo2dll.api.schema.DbService;
import com.ap.database.utils.pojo2dll.db.h2.H2DbService;
import com.ap.database.utils.pojo2dll.utils.JacksonMarshaller;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:schema.properties")
public class TestConfig {

    @Autowired
    Environment environment;

    @Bean
    static JacksonMarshaller getJacksonMarshaller() {
        return new JacksonMarshaller();
    }

    @Bean
    static DbService getDbService() {
        return new H2DbService();
    }

    @Bean
    public static DataSource dataSource() {
        // no need shutdown, EmbeddedDatabaseFactoryBean will take care of this
        return getDbService().build(EmbeddedDatabaseType.H2);
    }

    @Bean
    public static JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public Pojo2Ddl getPojo2Ddl(){
        return new Pojo2Ddl(environment.getProperty("pojo.list", String[].class ));
    }
}