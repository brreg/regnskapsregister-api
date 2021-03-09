package no.regnskap.utils;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import no.regnskap.Application;
import no.regnskap.TestData;
import no.regnskap.TestUtils;
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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;


@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
@WebAppConfiguration
public class EmbeddedPostgresBase {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmbeddedPostgresBase.class);

    private static final Slf4jLogConsumer postgresLog = new Slf4jLogConsumer(LOGGER).withPrefix("postgres-container");
    private static final Slf4jLogConsumer sftpLog = new Slf4jLogConsumer(LOGGER).withPrefix("sftp-container");

    @Autowired
    private ConnectionManager connectionManager;

    @Container
    private static final GenericContainer sftpContainer = new GenericContainer<>("atmoz/sftp")
            .withEnv(TestData.SFTP_ENV_VALUES)
            .withLogConsumer(sftpLog)
            .withExposedPorts(TestData.SFTP_PORT)
            .waitingFor(Wait.defaultWaitStrategy());

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
                e.printStackTrace();
            }

            TestPropertyValues.of(
                    "spring.datasource.url=" + embeddedPostgres.getJdbcUrl("postgres", "postgres"),
                    "spring.datasource.username=postgres",
                    "spring.datasource.password=postgres",
                    "spring.datasource.driver-class-name=org.postgresql.Driver",
                    "postgres.rreg.db_url=" + embeddedPostgres.getJdbcUrl("postgres", "postgres"),
                    "postgres.rreg.dbo_user=postgres",
                    "postgres.rreg.dbo_password=postgres",
                    "postgres.rreg.user=postgres",
                    "postgres.rreg.password=postgres",
                    "regnskap.sftp.host=" + sftpContainer.getContainerIpAddress(),
                    "regnskap.sftp.port=" + sftpContainer.getMappedPort(TestData.SFTP_PORT),
                    "regnskap.sftp.user=" + TestData.SFTP_USER,
                    "regnskap.sftp.password=" + TestData.SFTP_PWD,
                    "regnskap.sftp.directory=" + TestData.SFTP_DIR
            ).applyTo(configurableApplicationContext.getEnvironment());

            TestUtils.sftpUploadFile(sftpContainer.getContainerIpAddress(), sftpContainer.getMappedPort(TestData.SFTP_PORT), "log-test.xml");
        }
    }
}
