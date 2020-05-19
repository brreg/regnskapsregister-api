package no.regnskap.integration;

import no.regnskap.TestData;
import no.regnskap.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(LOGGER).withPrefix("mongo-container");
    private static Slf4jLogConsumer postgresLog = new Slf4jLogConsumer(LOGGER).withPrefix("postgres-container");
    private static Slf4jLogConsumer sftpLog = new Slf4jLogConsumer(LOGGER).withPrefix("sftp-container");

    @Container
    private static final GenericContainer sftpContainer = new GenericContainer<>("atmoz/sftp")
            .withEnv(TestData.SFTP_ENV_VALUES)
            .withLogConsumer(sftpLog)
            .withExposedPorts(TestData.SFTP_PORT)
            .waitingFor(Wait.defaultWaitStrategy());

    @Container
    private static final GenericContainer mongoContainer = new GenericContainer("mongo:latest")
            .withEnv(TestData.MONGO_ENV_VALUES)
            .withLogConsumer(mongoLog)
            .withExposedPorts(TestData.MONGO_PORT)
            .waitingFor(Wait.forListeningPort());

    @Container
    public static final PostgreSQLContainer postgreContainer = (PostgreSQLContainer)
            (new PostgreSQLContainer("postgres:latest")
                    .withDatabaseName(TestData.POSTGRES_DB_NAME)
                    .withUsername(TestData.POSTGRES_USER)
                    .withPassword(TestData.POSTGRES_PASSWORD)
                    .withLogConsumer(postgresLog)
                    .waitingFor(Wait.forListeningPort()));

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.data.mongodb.database=" + TestData.DATABASE_NAME,
                    "spring.data.mongodb.uri=" + TestData.buildMongoURI(mongoContainer.getContainerIpAddress(), mongoContainer.getMappedPort(TestData.MONGO_PORT), false),
                    "regnskap.sftp.host=" + sftpContainer.getContainerIpAddress(),
                    "regnskap.sftp.port=" + sftpContainer.getMappedPort(TestData.SFTP_PORT),
                    "regnskap.sftp.user=" + TestData.SFTP_USER,
                    "regnskap.sftp.password=" + TestData.SFTP_PWD,
                    "regnskap.sftp.directory=" + TestData.SFTP_DIR,
                    "spring.datasource.url=" + postgreContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreContainer.getUsername(),
                    "spring.datasource.password=" + postgreContainer.getPassword(),
                    "postgres.rreg.db_url=" + postgreContainer.getJdbcUrl(),
                    "postgres.rreg.dbo_user=" + postgreContainer.getUsername(),
                    "postgres.rreg.dbo_password=" + postgreContainer.getPassword(),
                    "postgres.rreg.user=" + postgreContainer.getUsername(),
                    "postgres.rreg.password=" + postgreContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());

            TestUtils.sftpUploadFile(sftpContainer.getContainerIpAddress(), sftpContainer.getMappedPort(TestData.SFTP_PORT), "log-test.xml");
        }
    }
}
