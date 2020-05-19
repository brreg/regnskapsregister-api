package no.regnskap.integration;

import no.regnskap.JenaResponseReader;
import no.regnskap.TestData;
import no.regnskap.controller.RegnskapApiImpl;
import no.regnskap.repository.RegnskapRepository;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ContextConfiguration(initializers = {RegnskapApiTest.Initializer.class})
@Tag("service")
class RegnskapApiTest extends TestContainersBase {
    private final static Logger logger = LoggerFactory.getLogger(RegnskapApiTest.class);

    private JenaResponseReader responseReader = new JenaResponseReader();

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Autowired
    private RegnskapApiImpl RegnskapApiImpl;

    @BeforeAll
    static void initDb(@Autowired RegnskapRepository repository) {
        repository.saveAll(TestData.DB_REGNSKAP_LIST);
        repository.save(TestData.dbRegnskapEmptyFields());
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

        ResponseEntity<Object> response = RegnskapApiImpl.getRegnskap(httpServletRequestMock, "orgnummer", 2018, null);
        ResponseEntity<Object> expected = new ResponseEntity<>(TestData.REGNSKAP_LIST, HttpStatus.OK);
        assertEquals(expected, response);

        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/rdf+xml");

        Object rdfResponse = RegnskapApiImpl.getRegnskap(httpServletRequestMock, "orgnummer", 2018, null).getBody();
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

        ResponseEntity<Object> response2018 = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, TestData.GENERATED_ID_2.toHexString());
        ResponseEntity<Object> response2017 = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, TestData.GENERATED_ID_0.toHexString());

        ResponseEntity<Object> expected2018 = new ResponseEntity<>(TestData.REGNSKAP_2018, HttpStatus.OK);
        ResponseEntity<Object> expected2017 = new ResponseEntity<>(TestData.REGNSKAP_2017, HttpStatus.OK);

        assertEquals(expected2018, response2018);
        assertEquals(expected2017, response2017);
    }

    @Test
    void noNullPointersFromAnyResponseType() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("text/turtle");
        HttpStatus turtleStatusCode = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, TestData.GENERATED_ID_3.toHexString()).getStatusCode();

        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/rdf+xml");
        HttpStatus rdfXmlStatusCode = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, TestData.GENERATED_ID_3.toHexString()).getStatusCode();

        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/ld+json");
        HttpStatus jsonLdStatusCode = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, TestData.GENERATED_ID_3.toHexString()).getStatusCode();

        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/json");
        HttpStatus jsonStatusCode = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, TestData.GENERATED_ID_3.toHexString()).getStatusCode();

        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        HttpStatus xmlStatusCode = RegnskapApiImpl.getRegnskapById(httpServletRequestMock, TestData.GENERATED_ID_3.toHexString()).getStatusCode();

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
