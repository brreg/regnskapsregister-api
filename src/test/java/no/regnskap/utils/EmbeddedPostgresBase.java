package no.regnskap.utils;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import no.regnskap.Application;
import no.regnskap.repository.ConnectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
@WebAppConfiguration
public class EmbeddedPostgresBase {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmbeddedPostgresBase.class);

    @Autowired
    private ConnectionManager connectionManager;

    private static EmbeddedPostgres embeddedPostgres;

    @BeforeEach
    public void updatePostgresDbUrl() {
        connectionManager.updateDbUrl(embeddedPostgres.getJdbcUrl("postgres", "postgres"));
    }

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            try {
                embeddedPostgres = EmbeddedPostgres.start();
            } catch (IOException e) {
                LOGGER.error("Failed starting embedded postgres database");
                e.printStackTrace();
            }

            TestPropertyValues.of(
                    "postgres.rreg.db_url=" + embeddedPostgres.getJdbcUrl("postgres", "postgres"),
                    "postgres.rreg.dbo_user=postgres",
                    "postgres.rreg.dbo_password=postgres",
                    "postgres.rreg.user=postgres",
                    "postgres.rreg.password=postgres"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
