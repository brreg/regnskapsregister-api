package no.brreg.regnskap.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class PostgresJdbcConfig {
    public static final String RREGAPIDB_DATASOURCE = "rregapidbDatasource";
    public static final String RREGAPIDB_JDBC_TEMPLATE = "rregapidbJdbcTemplate";

    @Bean(RREGAPIDB_DATASOURCE)
    @ConfigurationProperties("regnskap.rregapidb.datasource")
    public DataSource rregapidbDatasource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(RREGAPIDB_JDBC_TEMPLATE)
    public NamedParameterJdbcTemplate rregapidbJdbcTemplate(DataSource rregapidbDatasource) {
        return new NamedParameterJdbcTemplate(rregapidbDatasource);
    }

    @Bean
    @Qualifier("rregapidbTransactionManager")
    public DataSourceTransactionManager aardbTransactionManager(@Qualifier(RREGAPIDB_DATASOURCE) DataSource dataSource) {
        var transactionManager = new DataSourceTransactionManager(dataSource);
        transactionManager.setRollbackOnCommitFailure(true);

        return transactionManager;
    }
}
