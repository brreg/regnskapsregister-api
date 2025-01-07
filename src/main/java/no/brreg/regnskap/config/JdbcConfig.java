package no.brreg.regnskap.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty("regnskap.aarsregnskap-copy.enabled")
public class JdbcConfig {
    public static final String AARDB_DATASOURCE = "aardbDatasource";
    public static final String AARDB_JDBC_TEMPLATE = "aardbJdbcTemplate";

    @Bean(AARDB_DATASOURCE)
    @ConfigurationProperties("regnskap.aardb.datasource")
    public DataSource aardbDatasource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(AARDB_JDBC_TEMPLATE)
    public NamedParameterJdbcTemplate aardbJdbcTemplate(@Qualifier(AARDB_DATASOURCE) DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    @Qualifier("aardbTransactionManager")
    public DataSourceTransactionManager aardbTransactionManager(@Qualifier(AARDB_DATASOURCE) DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
