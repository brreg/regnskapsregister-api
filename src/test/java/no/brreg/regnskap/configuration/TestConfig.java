package no.brreg.regnskap.configuration;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import no.brreg.regnskap.Application;
import no.brreg.regnskap.TestData;
import no.brreg.regnskap.repository.AarsregnskapH2Repository;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.io.IOException;
import static no.brreg.regnskap.config.PostgresJdbcConfig.RREGAPIDB_DATASOURCE;

@Configuration
@Import({Application.class, AarsregnskapH2Repository.class})
@ContextConfiguration(initializers = {TestConfig.Initializer.class})
public class TestConfig {
    private static EmbeddedPostgres embeddedPostgres;

    @Primary
    @Bean(RREGAPIDB_DATASOURCE)
    public DataSource dataSource() throws IOException {
        embeddedPostgres = EmbeddedPostgres.builder().start();

        return embeddedPostgres.getPostgresDatabase();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "postgres.rreg.db_url=" + embeddedPostgres.getJdbcUrl(TestData.POSTGRES_USER, TestData.POSTGRES_DB_NAME),
                    "postgres.rreg.dbo_user=" + TestData.POSTGRES_USER,
                    "postgres.rreg.dbo_password=" + TestData.POSTGRES_PASSWORD,
                    "postgres.rreg.user=" + TestData.POSTGRES_USER,
                    "postgres.rreg.password=" + TestData.POSTGRES_PASSWORD
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
