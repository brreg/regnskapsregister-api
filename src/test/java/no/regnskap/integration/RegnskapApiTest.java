package no.regnskap.integration;

import no.regnskap.JenaResponseReader;
import no.regnskap.TestData;
import no.regnskap.TestUtils;
import no.regnskap.XmlTestData;
import no.regnskap.controller.RegnskapApiImpl;
import no.regnskap.generated.model.Regnskap;
import no.regnskap.repository.ConnectionManager;
import no.regnskap.repository.RegnskapLogRepository;
import no.regnskap.repository.RegnskapRepository;
import no.regnskap.utils.TestContainersBase;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ContextConfiguration(initializers = {TestContainersBase.Initializer.class})
@Tag("service")
public class RegnskapApiTest extends TestContainersBase {
    private final static Logger LOGGER = LoggerFactory.getLogger(HealthControllerTest.class);

    final static String TESTDATA_FILENAME = "xmlTestString";

    @Autowired
    private RegnskapApiImpl regnskapApiImpl;

    @Autowired
    private RegnskapLogRepository regnskapLogRepository;
    private static boolean hasImportedTestdata = false;
    private static Integer regnskap2018Id;

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
            InputStream testdataIS = new ByteArrayInputStream(XmlTestData.xmlTestString.getBytes(StandardCharsets.UTF_8));
            regnskapLogRepository.persistRegnskapFile(TESTDATA_FILENAME, testdataIS);

            regnskap2018Id = regnskapRepository.persistRegnskap(TestData.REGNSKAP_2018);

            //Add partner
            Connection connection = connectionManager.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO rreg.partners (name,key) VALUES ('test','test')")) {
                stmt.executeUpdate();
            }
            connection.commit();

            hasImportedTestdata = true;
        }
    }

    @Test
    public void getRegnskapTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        final String orgNummer = "980919676";
        final int år = 2018;
        ResponseEntity<Object> response = regnskapApiImpl.getRegnskap(httpServletRequestMock, orgNummer, år, "S");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Regnskap> body = (List<Regnskap>) response.getBody();
        for (Regnskap regnskap : body) {
            assertEquals(orgNummer, regnskap.getVirksomhet().getOrganisasjonsnummer());
            assertTrue(TestUtils.forYear(regnskap.getRegnskapsperiode(), år));
        }
    }

    @Test
    public void getRegnskapAcceptHeaderTest() {
        final String[] acceptHeaders = {"application/json", "application/xml", "application/ld+json",
                                        "application/rdf+json", "application/rdf+xml", "text/turtle"};
        for (String acceptHeader : acceptHeaders) {
            Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn(acceptHeader);
            ResponseEntity<Object> response = regnskapApiImpl.getRegnskap(httpServletRequestMock, "980919676", 2018, "S");
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    @Test
    public void getRegnskapByIdTest() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        final String id = "1";
        ResponseEntity<Object> response = regnskapApiImpl.getRegnskapById(httpServletRequestMock, id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Regnskap body = (Regnskap) response.getBody();
        assertEquals(id, body.getId());
    }

    @Test
    public void getRegnskapByIdPartnerTest() {
        Mockito.when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0"); // "Basic test:test"
        final String id = "1";
        ResponseEntity<Object> response = regnskapApiImpl.getRegnskapById(httpServletRequestMock, id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Regnskap body = (Regnskap) response.getBody();
        assertEquals(id, body.getId());
    }

    @Test
    public void getRegnskapByIdDefaultTurtleTest() throws IOException, SQLException {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("text/turtle");
        ResponseEntity<Object> response = regnskapApiImpl.getRegnskapById(httpServletRequestMock, regnskap2018Id.toString());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Model modelFromResponse = JenaResponseReader.parseResponse((String)response.getBody(), "TURTLE");
        Map<String,String> patches = new HashMap<>();
        patches.put("<identifier>", regnskap2018Id.toString());
        Model expectedResponse = JenaResponseReader.getExpectedResponse("OrgnrResponseDefault.ttl", patches, "TURTLE");

        boolean isIsomorphicWith = expectedResponse.isIsomorphicWith(modelFromResponse);
        if (!isIsomorphicWith) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                modelFromResponse.write(baos, "TURTLE");
                String s = new String(baos.toByteArray(), StandardCharsets.UTF_8);
                LOGGER.info("Response differ. Got:\n" + s);
            }
        }

        assertTrue(isIsomorphicWith);
    }

    @Test
    public void getRegnskapByIdPartnerTurtleTest() throws IOException, SQLException {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("text/turtle");
        Mockito.when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0"); // "Basic test:test"
        ResponseEntity<Object> response = regnskapApiImpl.getRegnskapById(httpServletRequestMock, regnskap2018Id.toString());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Model modelFromResponse = JenaResponseReader.parseResponse((String)response.getBody(), "TURTLE");
        Map<String,String> patches = new HashMap<>();
        patches.put("<identifier>", regnskap2018Id.toString());
        Model expectedResponse = JenaResponseReader.getExpectedResponse("OrgnrResponsePartner.ttl", patches, "TURTLE");

        boolean isIsomorphicWith = expectedResponse.isIsomorphicWith(modelFromResponse);
        if (!isIsomorphicWith) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                modelFromResponse.write(baos, "TURTLE");
                String s = new String(baos.toByteArray(), StandardCharsets.UTF_8);
                LOGGER.info("Response differ. Got:\n" + s);
            }
        }

        assertTrue(isIsomorphicWith);
    }

    @Test
    void correctSumsInPersistenceFieldsDefault() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        ResponseEntity<Object> response = regnskapApiImpl.getRegnskapById(httpServletRequestMock, regnskap2018Id.toString());
        Regnskap regnskap = (Regnskap) response.getBody();

        long baseValue = 2018L;
        assertNull(regnskap.getEiendeler().getGoodwill());
        assertNull(regnskap.getEiendeler().getSumVarer());
        assertNull(regnskap.getEiendeler().getSumFordringer());
        assertNull(regnskap.getEiendeler().getSumInvesteringer());
        assertNull(regnskap.getEiendeler().getSumBankinnskuddOgKontanter());
        assertEquals(baseValue + 5, regnskap.getEiendeler().getSumEiendeler().intValue());
        assertEquals(baseValue + 6, regnskap.getEiendeler().getAnleggsmidler().getSumAnleggsmidler().intValue());
        assertEquals(baseValue + 7, regnskap.getEiendeler().getOmloepsmidler().getSumOmloepsmidler().intValue());
        assertEquals(baseValue + 8, regnskap.getEgenkapitalGjeld().getSumEgenkapitalGjeld().intValue());
        assertEquals(baseValue + 9, regnskap.getEgenkapitalGjeld().getEgenkapital().getSumEgenkapital().intValue());
        assertEquals(baseValue + 10, regnskap.getEgenkapitalGjeld().getEgenkapital().getInnskuttEgenkapital().getSumInnskuttEgenkaptial().intValue());
        assertEquals(baseValue + 11, regnskap.getEgenkapitalGjeld().getEgenkapital().getOpptjentEgenkapital().getSumOpptjentEgenkapital().intValue());
        assertEquals(baseValue + 12, regnskap.getEgenkapitalGjeld().getGjeldOversikt().getSumGjeld().intValue());
        assertEquals(baseValue + 13, regnskap.getEgenkapitalGjeld().getGjeldOversikt().getKortsiktigGjeld().getSumKortsiktigGjeld().intValue());
        assertEquals(baseValue + 14, regnskap.getEgenkapitalGjeld().getGjeldOversikt().getLangsiktigGjeld().getSumLangsiktigGjeld().intValue());
        assertEquals(baseValue + 15, regnskap.getResultatregnskapResultat().getOrdinaertResultatFoerSkattekostnad().intValue());
        assertNull(regnskap.getResultatregnskapResultat().getOrdinaertResultatSkattekostnad());
        assertNull(regnskap.getResultatregnskapResultat().getEkstraordinaerePoster());
        assertNull(regnskap.getResultatregnskapResultat().getSkattekostnadEkstraordinaertResultat());
        assertEquals(baseValue + 19, regnskap.getResultatregnskapResultat().getAarsresultat().intValue());
        assertEquals(baseValue + 20, regnskap.getResultatregnskapResultat().getTotalresultat().intValue());
        assertEquals(baseValue + 21, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsresultat().intValue());
        assertNull(regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().getSalgsinntekter());
        assertEquals(baseValue + 23, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().getSumDriftsinntekter().intValue());
        assertNull(regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().getLoennskostnad());
        assertEquals(baseValue + 25, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().getSumDriftskostnad().intValue());
        assertEquals(baseValue + 26, regnskap.getResultatregnskapResultat().getFinansresultat().getNettoFinans().intValue());
        assertEquals(baseValue + 27, regnskap.getResultatregnskapResultat().getFinansresultat().getFinansinntekt().getSumFinansinntekter().intValue());
        assertNull(regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getRentekostnadSammeKonsern());
        assertNull(regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getAnnenRentekostnad());
        assertEquals(baseValue + 30, regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getSumFinanskostnad().intValue());
    }

    @Test
    void correctSumsInPersistenceFieldsPartner() {
        Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        Mockito.when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0"); // "Basic test:test"
        ResponseEntity<Object> response = regnskapApiImpl.getRegnskapById(httpServletRequestMock, regnskap2018Id.toString());
        Regnskap regnskap = (Regnskap) response.getBody();

        long baseValue = 2018L;
        assertEquals(baseValue, regnskap.getEiendeler().getGoodwill().intValue());
        assertEquals(baseValue + 1, regnskap.getEiendeler().getSumVarer().intValue());
        assertEquals(baseValue + 2, regnskap.getEiendeler().getSumFordringer().intValue());
        assertEquals(baseValue + 3, regnskap.getEiendeler().getSumInvesteringer().intValue());
        assertEquals(baseValue + 4, regnskap.getEiendeler().getSumBankinnskuddOgKontanter().intValue());
        assertEquals(baseValue + 5, regnskap.getEiendeler().getSumEiendeler().intValue());
        assertEquals(baseValue + 6, regnskap.getEiendeler().getAnleggsmidler().getSumAnleggsmidler().intValue());
        assertEquals(baseValue + 7, regnskap.getEiendeler().getOmloepsmidler().getSumOmloepsmidler().intValue());
        assertEquals(baseValue + 8, regnskap.getEgenkapitalGjeld().getSumEgenkapitalGjeld().intValue());
        assertEquals(baseValue + 9, regnskap.getEgenkapitalGjeld().getEgenkapital().getSumEgenkapital().intValue());
        assertEquals(baseValue + 10, regnskap.getEgenkapitalGjeld().getEgenkapital().getInnskuttEgenkapital().getSumInnskuttEgenkaptial().intValue());
        assertEquals(baseValue + 11, regnskap.getEgenkapitalGjeld().getEgenkapital().getOpptjentEgenkapital().getSumOpptjentEgenkapital().intValue());
        assertEquals(baseValue + 12, regnskap.getEgenkapitalGjeld().getGjeldOversikt().getSumGjeld().intValue());
        assertEquals(baseValue + 13, regnskap.getEgenkapitalGjeld().getGjeldOversikt().getKortsiktigGjeld().getSumKortsiktigGjeld().intValue());
        assertEquals(baseValue + 14, regnskap.getEgenkapitalGjeld().getGjeldOversikt().getLangsiktigGjeld().getSumLangsiktigGjeld().intValue());
        assertEquals(baseValue + 15, regnskap.getResultatregnskapResultat().getOrdinaertResultatFoerSkattekostnad().intValue());
        assertEquals(baseValue + 16, regnskap.getResultatregnskapResultat().getOrdinaertResultatSkattekostnad().intValue());
        assertEquals(baseValue + 17, regnskap.getResultatregnskapResultat().getEkstraordinaerePoster().intValue());
        assertEquals(baseValue + 18, regnskap.getResultatregnskapResultat().getSkattekostnadEkstraordinaertResultat().intValue());
        assertEquals(baseValue + 19, regnskap.getResultatregnskapResultat().getAarsresultat().intValue());
        assertEquals(baseValue + 20, regnskap.getResultatregnskapResultat().getTotalresultat().intValue());
        assertEquals(baseValue + 21, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsresultat().intValue());
        assertEquals(baseValue + 22, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().getSalgsinntekter().intValue());
        assertEquals(baseValue + 23, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().getSumDriftsinntekter().intValue());
        assertEquals(baseValue + 24, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().getLoennskostnad().intValue());
        assertEquals(baseValue + 25, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().getSumDriftskostnad().intValue());
        assertEquals(baseValue + 26, regnskap.getResultatregnskapResultat().getFinansresultat().getNettoFinans().intValue());
        assertEquals(baseValue + 27, regnskap.getResultatregnskapResultat().getFinansresultat().getFinansinntekt().getSumFinansinntekter().intValue());
        assertEquals(baseValue + 28, regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getRentekostnadSammeKonsern().intValue());
        assertEquals(baseValue + 29, regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getAnnenRentekostnad().intValue());
        assertEquals(baseValue + 30, regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getSumFinanskostnad().intValue());
    }

    @Test
    public void getLogTest() {
        ResponseEntity<List<String>> response = regnskapApiImpl.getLog(httpServletRequestMock);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<String> body = response.getBody();
        assertTrue(body.contains(TESTDATA_FILENAME));
    }

}
