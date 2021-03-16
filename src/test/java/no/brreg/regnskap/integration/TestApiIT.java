package no.brreg.regnskap.integration;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import no.brreg.regnskap.TestData;
import no.brreg.regnskap.XmlTestData;
import no.brreg.regnskap.controller.TestApiImpl;
import no.brreg.regnskap.repository.ConnectionManager;
import no.brreg.regnskap.repository.RegnskapLogRepository;
import no.brreg.regnskap.repository.RegnskapRepository;

import no.brreg.regnskap.utils.EmbeddedPostgresIT;

public class TestApiIT extends EmbeddedPostgresIT {
    
    final static String TESTDATA_FILENAME = "xmlTestString";

    @Autowired
    private TestApiImpl testApiImpl;

    @Autowired
    private RegnskapLogRepository regnskapLogRepository;
    
    private static boolean hasImportedTestdata = false;
    private static Integer regnskapId1;
    private static Integer regnskapId2;

    @Autowired
    private RegnskapRepository regnskapRepository;

    @Autowired
    private ConnectionManager connectionManager;

    @Mock
    HttpServletRequest httpServletRequestMock;


    @BeforeEach
    void resetMocks() throws IOException, SQLException {
        Mockito.reset(
            httpServletRequestMock
        );

        if (!hasImportedTestdata) {

            regnskapId1 = regnskapRepository.persistRegnskap(TestData.REGNSKAP_2_2016S);
            regnskapId2 = regnskapRepository.persistRegnskap(TestData.REGNSKAP_3_2016S);

            Connection connection = connectionManager.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO rregapi.partners (name,key) VALUES ('test','test')")) {
                stmt.executeUpdate();
            }
            connection.commit();

            hasImportedTestdata = true;
        }
    }
    
    @Test
    public void orgnrOfLastAddedRegnskapIsReturned() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        ResponseEntity<String> response = testApiImpl.getMostRecent(httpServletRequestMock);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), TestData.TEST_ORGNR_3);
    }
}
