package no.brreg.regnskap.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static no.brreg.regnskap.config.PostgresJdbcConfig.RREGAPIDB_DATASOURCE;

@Configuration
public class LiquibaseConfig {

    public final DataSource rregapidb;

    public LiquibaseConfig(@Qualifier(RREGAPIDB_DATASOURCE) DataSource rregapidb) {
        this.rregapidb = rregapidb;
    }

    @Bean
    public SpringLiquibase liquibase() {
        var liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:liquibase/changelog/changelog-master.xml");
        liquibase.setDataSource(rregapidb);
        return liquibase;
    }
}
