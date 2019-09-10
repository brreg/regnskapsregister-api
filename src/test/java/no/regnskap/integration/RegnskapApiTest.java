package no.regnskap.integration;

import no.regnskap.TestUtils;
import no.regnskap.controller.RegnskapApiImpl;
import no.regnskap.generated.model.Regnskap;
import no.regnskap.repository.RegnskapRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static no.regnskap.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = {RegnskapApiTest.Initializer.class})
@Tag("service")
class RegnskapApiTest {
    private final static Logger logger = LoggerFactory.getLogger(RegnskapApiTest.class);
    private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");
    private static Slf4jLogConsumer sftpLog = new Slf4jLogConsumer(logger).withPrefix("sftp-container");

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Autowired
    private RegnskapApiImpl RegnskapApiImpl;

    @Container
    private static final GenericContainer sftpContainer = new GenericContainer<>("atmoz/sftp")
        .withEnv(SFTP_ENV_VALUES)
        .withLogConsumer(sftpLog)
        .withExposedPorts(SFTP_PORT)
        .waitingFor(Wait.defaultWaitStrategy());

    @Container
    private static final GenericContainer mongoContainer = new GenericContainer("mongo:latest")
        .withEnv(MONGO_ENV_VALUES)
        .withLogConsumer(mongoLog)
        .withExposedPorts(MONGO_PORT)
        .waitingFor(Wait.forListeningPort());

    static class Initializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "spring.data.mongodb.database=" + DATABASE_NAME,
                "spring.data.mongodb.uri=" + buildMongoURI(mongoContainer.getContainerIpAddress(), mongoContainer.getMappedPort(MONGO_PORT), false),
                "regnskap.sftp.host=" + sftpContainer.getContainerIpAddress(),
                "regnskap.sftp.port=" + sftpContainer.getMappedPort(SFTP_PORT),
                "regnskap.sftp.user=" + SFTP_USER,
                "regnskap.sftp.password=" + SFTP_PWD,
                "regnskap.sftp.directory=" + SFTP_DIR
            ).applyTo(configurableApplicationContext.getEnvironment());

            TestUtils.sftpUploadFile(sftpContainer.getContainerIpAddress(), sftpContainer.getMappedPort(SFTP_PORT), "log-test.xml");
        }
    }

    @BeforeAll
    static void initDb(@Autowired RegnskapRepository repository) {
        repository.saveAll(DB_REGNSKAP_LIST);
    }

    @Test
    void ping() {
        ResponseEntity<String> response = RegnskapApiImpl.getPing();
        ResponseEntity<String> expected = new ResponseEntity<>("pong", HttpStatus.OK);
        assertEquals(expected, response);
    }

    @Test
    void getByOrgnr() {
        ResponseEntity<List<Regnskap>> response = RegnskapApiImpl.getRegnskap(httpServletRequestMock, "orgnummer");
        ResponseEntity<List<Regnskap>> expected = new ResponseEntity<>(REGNSKAP_LIST, HttpStatus.OK);
        assertEquals(expected, response);
    }

    @Test
    void getById() {
        ResponseEntity<Regnskap> response2018 = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, GENERATED_ID_2.toHexString());
        ResponseEntity<Regnskap> response2017 = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, GENERATED_ID_0.toHexString());

        ResponseEntity<Regnskap> expected2018 = new ResponseEntity<>(REGNSKAP_2018, HttpStatus.OK);
        ResponseEntity<Regnskap> expected2017 = new ResponseEntity<>(REGNSKAP_2017, HttpStatus.OK);

        assertEquals(expected2018, response2018);
        assertEquals(expected2017, response2017);
    }

    @Test
    void getLog() {
        List<String> expectedLogList = new ArrayList<>();
        expectedLogList.add("log-test.xml");

        ResponseEntity<List<String>> response = RegnskapApiImpl.getLog(httpServletRequestMock);
        ResponseEntity<List<String>> expected = new ResponseEntity<>(expectedLogList, HttpStatus.OK);
        assertEquals(expected, response);
    }
}
