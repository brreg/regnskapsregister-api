package no.regnskap.integration;

import no.regnskap.JenaResponseReader;
import no.regnskap.TestUtils;
import no.regnskap.controller.RegnskapApiImpl;
import no.regnskap.model.RegnskapDB;
import no.regnskap.repository.RegnskapRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static no.regnskap.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = {RegnskapApiTest.Initializer.class})
@Tag("service")
class RegnskapApiTest {
    private final static Logger logger = LoggerFactory.getLogger(RegnskapApiTest.class);
    private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");
    private static Slf4jLogConsumer sftpLog = new Slf4jLogConsumer(logger).withPrefix("sftp-container");
    private JenaResponseReader responseReader = new JenaResponseReader();

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
        repository.save(dbRegnskapEmptyFields());
    }

    @BeforeEach
    void resetMock() {
        Mockito.reset(httpServletRequestMock);
    }

    @Test
    void ping() {
        ResponseEntity<String> response = RegnskapApiImpl.getPing();
        ResponseEntity<String> expected = new ResponseEntity<>("pong", HttpStatus.OK);
        assertEquals(expected, response);
    }

    @Test
    void getByOrgnr() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/json");

        ResponseEntity<Object> response = RegnskapApiImpl.getRegnskap(httpServletRequestMock, "orgnummer", null, null);
        ResponseEntity<Object> expected = new ResponseEntity<>(REGNSKAP_LIST, HttpStatus.OK);
        assertEquals(expected, response);

        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/rdf+xml");

        Object rdfResponse = RegnskapApiImpl.getRegnskap(httpServletRequestMock, "orgnummer", null, null).getBody();
        Model modelFromResponse = responseReader.parseResponse((String)rdfResponse, "RDFXML");
        Model expectedResponse = responseReader.getExpectedResponse("OrgnrResponse.ttl", "TURTLE");

/*        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            modelFromResponse.write(baos, new RDFFormat(Lang.TURTLE).toString());
            String s = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            int d = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
    }

    @Test
    void getById() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");

        ResponseEntity<Object> response2018 = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, GENERATED_ID_2.toHexString());
        ResponseEntity<Object> response2017 = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, GENERATED_ID_0.toHexString());

        ResponseEntity<Object> expected2018 = new ResponseEntity<>(REGNSKAP_2018, HttpStatus.OK);
        ResponseEntity<Object> expected2017 = new ResponseEntity<>(REGNSKAP_2017, HttpStatus.OK);

        assertEquals(expected2018, response2018);
        assertEquals(expected2017, response2017);
    }

    @Test
    void noNullPointersFromAnyResponseType() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("text/turtle");
        HttpStatus turtleStatusCode = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, GENERATED_ID_3.toHexString()).getStatusCode();

        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/rdf+xml");
        HttpStatus rdfXmlStatusCode = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, GENERATED_ID_3.toHexString()).getStatusCode();

        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/ld+json");
        HttpStatus jsonLdStatusCode = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, GENERATED_ID_3.toHexString()).getStatusCode();

        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/json");
        HttpStatus jsonStatusCode = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, GENERATED_ID_3.toHexString()).getStatusCode();

        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        HttpStatus xmlStatusCode = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, GENERATED_ID_3.toHexString()).getStatusCode();

        assertEquals(HttpStatus.OK, turtleStatusCode);
        assertEquals(HttpStatus.OK, rdfXmlStatusCode);
        assertEquals(HttpStatus.OK, jsonLdStatusCode);
        assertEquals(HttpStatus.OK, jsonStatusCode);
        assertEquals(HttpStatus.OK, xmlStatusCode);
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
