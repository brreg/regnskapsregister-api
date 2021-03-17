package no.brreg.regnskap.utils;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import no.brreg.regnskap.Application;
import no.brreg.regnskap.TestData;
import no.brreg.regnskap.repository.ConnectionManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
@WebAppConfiguration
@ContextConfiguration(initializers = {EmbeddedPostgresIT.Initializer.class})
public abstract class EmbeddedPostgresIT {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmbeddedPostgresIT.class);

    @Autowired
    private ConnectionManager connectionManager;

    private static EmbeddedPostgres embeddedPostgres;

    @BeforeEach
    public void updatePostgresDbUrl() {
        connectionManager.updateDbUrl(embeddedPostgres.getJdbcUrl(TestData.POSTGRES_USER, TestData.POSTGRES_DB_NAME));
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            try {
                embeddedPostgres = EmbeddedPostgres.start();
            } catch (IOException e) {
                LOGGER.error("Failed starting embedded postgres database");
                e.printStackTrace();
            }

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
