package no.brreg.regnskap.integration;

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
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import no.brreg.regnskap.controller.RegnskapApiImpl;
import no.brreg.regnskap.controller.StatistikkApiImpl;
import no.brreg.regnskap.repository.ConnectionManager;
import no.brreg.regnskap.repository.RegnskapLogRepository;
import no.brreg.regnskap.repository.RegnskapRepository;
import no.brreg.regnskap.utils.EmbeddedPostgresSetup;

import no.brreg.regnskap.TestData;
import no.brreg.regnskap.TestUtils;
import no.brreg.regnskap.XmlTestData;

import static org.junit.jupiter.api.Assertions.*;

public class StatistikkApiIT extends EmbeddedPostgresSetup {
    private final static Logger LOGGER = LoggerFactory.getLogger(StatistikkApiIT.class);

    final static String TESTDATA_FILENAME = "xmlTestString";

    @Autowired
    private StatistikkApiImpl statistikkApiImpl;

    @Autowired
    private RegnskapApiImpl regnskapApiImpl;

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    private RegnskapRepository regnskapRepository;

    @Autowired
    private RegnskapLogRepository regnskapLogRepository;

    @Mock
    HttpServletRequest httpServletRequestMock;

    private static Integer regnskap2016Id;
    private static Integer regnskap2017Id;
    private static Integer regnskap2018_1Id;
    private static Integer regnskap2018_2Id;
    private static Integer regnskap2018_3Id;
    private static Integer regnskap2019_1Id;
    private static Integer regnskap2019_2Id;

    @BeforeEach
    void resetMocks() throws IOException, SQLException {
        Mockito.reset(
            httpServletRequestMock
        );

        InputStream testdataIS = new ByteArrayInputStream(XmlTestData.xmlTestString.getBytes(StandardCharsets.UTF_8));
        try {
            regnskapLogRepository.persistRegnskapFile(TESTDATA_FILENAME, testdataIS);
        } catch (SQLException e) {
            LOGGER.info("Regnskap file test data already loaded");
        }

        regnskap2016Id = regnskapRepository.persistRegnskap(TestData.REGNSKAP_2016S);
        regnskap2017Id = regnskapRepository.persistRegnskap(TestData.REGNSKAP_2017S);
        regnskap2018_1Id = regnskapRepository.persistRegnskap(TestData.REGNSKAP_2018_1S);
        regnskap2018_2Id = regnskapRepository.persistRegnskap(TestData.REGNSKAP_2018_2S);
        regnskap2018_3Id = regnskapRepository.persistRegnskap(TestData.REGNSKAP_2018_3K);
        regnskap2019_1Id = regnskapRepository.persistRegnskap(TestData.REGNSKAP_2019_1S);
        regnskap2019_2Id = regnskapRepository.persistRegnskap(TestData.REGNSKAP_2019_2K);

        //Add partner
        Connection connection = connectionManager.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO rregapi.partners (name,key) VALUES ('test','test')")) {
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            LOGGER.info("Partner test data already loaded");
        }
    }

    @Test
    public void getStatisticsByOrgnrTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        final String orgNummer = TestData.TEST_ORGNR_1;
        
        //Simulate traffic to API
        for(int i = 0; i < 10; i++) {
            ResponseEntity<Object> dummy = regnskapApiImpl.getRegnskap(
                httpServletRequestMock, orgNummer, null, null);
        }
        
        ResponseEntity<List<String>> response = statistikkApiImpl.getStatisticsByOrgnr(
            httpServletRequestMock, null, null);
        List<String> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, body.size());
        assertEquals("10;123456789", body.get(0));
    }

    @Test
    public void getStatisticsByMethodTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        final String orgNummer = TestData.TEST_ORGNR_1;
        
        //Simulate traffic to API
        for(int i = 0; i < 10; i++) {
            ResponseEntity<Object> dummy = regnskapApiImpl.getRegnskap(
                httpServletRequestMock, orgNummer, null, null);
        }
        
        ResponseEntity<List<String>> response = statistikkApiImpl.getStatisticsByMethod(
            httpServletRequestMock, null, null);
        List<String> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, body.size());
        assertEquals("10;getRegnskap", body.get(0));
    }

    @Test
    public void getStatisticsByIpTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        final String orgNummer = TestData.TEST_ORGNR_1;
        
        //Simulate traffic to API
        for(int i = 0; i < 10; i++) {
            ResponseEntity<Object> dummy = regnskapApiImpl.getRegnskap(
                httpServletRequestMock, orgNummer, null, null);
        }
        
        ResponseEntity<List<String>> response = statistikkApiImpl.getStatisticsByIp(
            httpServletRequestMock, null, null);
        List<String> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, body.size());
        //no ip is recorded when called internally, but count should be correct
        assertEquals("10;null", body.get(0));
    }
    
}
