package no.brreg.regnskap.integration;

import jakarta.servlet.http.HttpServletRequest;
import no.brreg.regnskap.TestData;
import no.brreg.regnskap.controller.TestApiImpl;
import no.brreg.regnskap.repository.RegnskapRepository;
import no.brreg.regnskap.utils.EmbeddedPostgresSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestApiIT extends EmbeddedPostgresSetup {
    private final static Logger LOGGER = LoggerFactory.getLogger(TestApiIT.class);

    final static String TESTDATA_FILENAME = "xmlTestString";

    @Autowired
    private TestApiImpl testApiImpl;

    private static Integer regnskapId1;
    private static Integer regnskapId2;

    @Autowired
    private RegnskapRepository regnskapRepository;


    @Mock
    HttpServletRequest httpServletRequestMock;


    @BeforeEach
    void resetMocks() throws IOException, SQLException {
        Mockito.reset(
            httpServletRequestMock
        );

        regnskapId1 = regnskapRepository.persistRegnskap(TestData.REGNSKAP_2_2015S);
        regnskapId2 = regnskapRepository.persistRegnskap(TestData.REGNSKAP_3_2015S);
    }

    @Test
    public void orgnrOfLastAddedRegnskapIsReturned() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        ResponseEntity<String> response = testApiImpl.getMostRecent(httpServletRequestMock);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), TestData.TEST_ORGNR_3);
    }
}
