package no.brreg.regnskap.utils;

import no.brreg.regnskap.TestData;
import no.brreg.regnskap.TestUtils;
import no.brreg.regnskap.repository.ConnectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
public class TestContainersBase {
    private final static Logger LOGGER = LoggerFactory.getLogger(TestContainersBase.class);

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

    @Container
    public static final PostgreSQLContainer postgreSQLContainer = (PostgreSQLContainer)
            (new PostgreSQLContainer()
                .withDatabaseName(TestData.POSTGRES_DB_NAME)
                .withUsername(TestData.POSTGRES_USER)
                .withPassword(TestData.POSTGRES_PASSWORD)
                .withLogConsumer(postgresLog)
                /*.waitingFor(Wait.defaultWaitStrategy())*/);

    @BeforeEach
    public void updatePostgresDbUrl() {
        connectionManager.updateDbUrl(postgreSQLContainer.getJdbcUrl());
    }

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "postgres.rreg.db_url=" + postgreSQLContainer.getJdbcUrl(),
                    "postgres.rreg.dbo_user=" + postgreSQLContainer.getUsername(),
                    "postgres.rreg.dbo_password=" + postgreSQLContainer.getPassword(),
                    "postgres.rreg.user=" + postgreSQLContainer.getUsername(),
                    "postgres.rreg.password=" + postgreSQLContainer.getPassword(),
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
