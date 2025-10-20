package no.brreg.regnskap.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import no.brreg.regnskap.*;
import no.brreg.regnskap.controller.RegnskapApiImpl;
import no.brreg.regnskap.generated.model.Regnskap;
import no.brreg.regnskap.generated.model.Regnskapsprinsipper;
import no.brreg.regnskap.generated.model.Regnskapstype;
import no.brreg.regnskap.repository.RegnskapLogRepository;
import no.brreg.regnskap.repository.RegnskapRepository;
import no.brreg.regnskap.utils.EmbeddedPostgresSetup;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
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
import java.util.stream.Stream;

import static no.brreg.regnskap.TestData.TEST_ORGNR_1;
import static no.brreg.regnskap.config.PostgresJdbcConfig.RREGAPIDB_DATASOURCE;
import static no.brreg.regnskap.generated.model.Regnskapstype.KONSERN;
import static no.brreg.regnskap.generated.model.Regnskapstype.SELSKAP;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class RegnskapApiIT extends EmbeddedPostgresSetup {
    private final static Logger LOGGER = LoggerFactory.getLogger(RegnskapApiIT.class);

    final static String TESTDATA_FILENAME = "xmlTestString";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegnskapApiImpl regnskapApiImpl;

    @Autowired
    private RegnskapLogRepository regnskapLogRepository;

    private static Integer regnskap2016Id;
    private static Integer regnskap2017Id;
    private static Integer regnskap2018_1Id;
    private static Integer regnskap2018_2Id;
    private static Integer regnskap2018_3Id;
    private static Integer regnskap2019_1Id;
    private static Integer regnskap2019_2Id;

    @Autowired
    private RegnskapRepository regnskapRepository;

    @Autowired
    @Qualifier(RREGAPIDB_DATASOURCE)
    private DataSource dataSource;

    @Mock
    HttpServletRequest httpServletRequestMock;


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
        Connection connection = dataSource.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO rregapi.partners (name,key) VALUES ('test','test')")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.info("Partner test data already loaded");
        }
    }

    @Test
    public void regnskapsreglerEnumTest() {
        Regnskapsprinsipper.RegnskapsreglerEnum regler = TestData.REGNSKAP_2018_1S.getRegnkapsprinsipper().getRegnskapsregler();
        assertTrue(regler == Regnskapsprinsipper.RegnskapsreglerEnum.REGNSKAPSLOVEN_ALMINNELIG_REGLER);
        assertFalse(regler == Regnskapsprinsipper.RegnskapsreglerEnum.FORENKLET_ANVENDELSE_IFRS);
        assertFalse(regler == Regnskapsprinsipper.RegnskapsreglerEnum.IFRS);
    }

    @Test
    public void getRegnskapTest() {
        when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        final String orgNummer = TEST_ORGNR_1;
        //Get most recent SELSKAP regnskap for TestData.TEST_ORGNR_1
        ResponseEntity response = regnskapApiImpl.getRegnskap(httpServletRequestMock, orgNummer, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Regnskap> body = (List<Regnskap>) response.getBody();
        assertEquals(1, body.size());
        assertEquals(orgNummer, body.get(0).getVirksomhet().getOrganisasjonsnummer());
        assertEquals(2019, body.get(0).getRegnskapsperiode().getFraDato().getYear());
        assertEquals(SELSKAP, body.get(0).getRegnskapstype());
    }

    @Test
    public void getRegnskapPartnerTest() {
        when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0"); // "Basic test:test"
        final String orgNummer = TEST_ORGNR_1;
        //Get most recent regnskap of any type for three most recent years for TestData.TEST_ORGNR_1
        ResponseEntity response = regnskapApiImpl.getRegnskap(httpServletRequestMock, orgNummer, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Regnskap> actualRegnskap = (List<Regnskap>) response.getBody();

        assertEquals(5, actualRegnskap.size());

        assertEquals("201803", actualRegnskap.get(0).getJournalnr());
        assertEquals(2018, actualRegnskap.get(0).getRegnskapsperiode().getFraDato().getYear());
        assertEquals(KONSERN, actualRegnskap.get(0).getRegnskapstype());

        assertEquals("201901", actualRegnskap.get(1).getJournalnr());
        assertEquals(2019, actualRegnskap.get(1).getRegnskapsperiode().getFraDato().getYear());
        assertEquals(KONSERN, actualRegnskap.get(1).getRegnskapstype());

        assertEquals("201701", actualRegnskap.get(2).getJournalnr());
        assertEquals(2017, actualRegnskap.get(2).getRegnskapsperiode().getFraDato().getYear());
        assertEquals(SELSKAP, actualRegnskap.get(2).getRegnskapstype());

        assertEquals("201802", actualRegnskap.get(3).getJournalnr());
        assertEquals(2018, actualRegnskap.get(3).getRegnskapsperiode().getFraDato().getYear());
        assertEquals(SELSKAP, actualRegnskap.get(3).getRegnskapstype());

        assertEquals("201901", actualRegnskap.get(4).getJournalnr());
        assertEquals(2019, actualRegnskap.get(4).getRegnskapsperiode().getFraDato().getYear());
        assertEquals(SELSKAP, actualRegnskap.get(4).getRegnskapstype());
    }

    @Test
    public void getRegnskapInvalidPartnerTest() {
        when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Basic d3Jvbmc6cGFzc3dvcmQ="); // "Basic wrong:password"
        final String orgNummer = TEST_ORGNR_1;
        ResponseEntity response = regnskapApiImpl.getRegnskap(httpServletRequestMock, orgNummer, null, null);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        List<Regnskap> body = (List<Regnskap>) response.getBody();
        assertNull(body);
    }

    @Test
    public void getRegnskapPartner2018SelskapTest() {
        when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0"); // "Basic test:test"
        final String orgNummer = TEST_ORGNR_1;
        final int år = 2018;
        final Regnskapstype regnskapstype = SELSKAP;
        ResponseEntity response = regnskapApiImpl.getRegnskap(httpServletRequestMock, orgNummer, år, regnskapstype);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Regnskap> body = (List<Regnskap>) response.getBody();
        for (Regnskap regnskap : body) {
            assertEquals(orgNummer, regnskap.getVirksomhet().getOrganisasjonsnummer());
            assertTrue(TestUtils.forYear(regnskap.getRegnskapsperiode(), år));
        }
    }

    @Test
    public void getRegnskapPartner2018SelskapInvalidÅrTest() {
        when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0"); // "Basic test:test"
        final String orgNummer = TEST_ORGNR_1;
        final int år = Integer.parseInt(TEST_ORGNR_1);
        final Regnskapstype regnskapstype = SELSKAP;
        ResponseEntity response = regnskapApiImpl.getRegnskap(httpServletRequestMock, orgNummer, år, regnskapstype);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void getRegnskapAcceptHeaderTest() {
        final String[] acceptHeaders = {
                "application/json",
                "application/xml",
                "application/ld+json",
                "application/rdf+json",
                "application/rdf+xml",
                "text/turtle"
        };

        for (String acceptHeader : acceptHeaders) {
            when(httpServletRequestMock.getHeader("Accept")).thenReturn(acceptHeader);
            ResponseEntity response = regnskapApiImpl.getRegnskap(httpServletRequestMock, TEST_ORGNR_1, 2018, SELSKAP);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    @Test
    public void getRegnskapByIdTest() {
        when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        final String orgNummer = "980919676";
        final Integer id = 1;
        ResponseEntity<Regnskap> response = regnskapApiImpl.getRegnskapById(httpServletRequestMock, orgNummer, id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Regnskap body = response.getBody();
        assertEquals(id, body.getId());
    }

    @Test
    public void getRegnskapByIdPartnerTest() {
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0"); // "Basic test:test"
        final String orgNummer = "980919676";
        final Integer id = 1;
        ResponseEntity<Regnskap> response = regnskapApiImpl.getRegnskapById(httpServletRequestMock, orgNummer, id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Regnskap body =response.getBody();
        assertEquals(id, body.getId());
    }

    @ParameterizedTest
    @MethodSource("getRegnskapByOrgnummerDefaultTest_arguments")
    public void getRegnskapByOrgnummerDefaultTest(String acceptHeader, String expectedResponseFile) throws Exception {
        String expectedResponse = JenaResponseReader.resourceAsString(expectedResponseFile);
        mockMvc
                .perform(get("/regnskapsregisteret/regnskap/123456789").header("accept", acceptHeader))
                .andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith(acceptHeader))
                .andExpect(content().string(expectedResponse));
    }

    public static Stream<Arguments> getRegnskapByOrgnummerDefaultTest_arguments() {
        return Stream.of(
                arguments("application/json", "regnskap/OrgnrResponseDefault.json"),
                arguments("application/rdf+json", "regnskap/OrgnrResponseDefault.rdf.json"),
                arguments("application/xml", "regnskap/OrgnrResponseDefault.xml")
        );
    }

    @Test
    public void getRegnskapByOrgnummerWithInvalidOppstillingsplanTest() throws Exception {
        try {
            InputStream inputStream = new ByteArrayInputStream(XmlTestDataInvalidOppstillingsplan.xmlTestString.getBytes(StandardCharsets.UTF_8));
            regnskapLogRepository.persistRegnskapFile("testFile2", inputStream);
        } catch (SQLException e) {
            LOGGER.info("Regnskap file test data already loaded");
        }

        mockMvc
                .perform(get("/regnskapsregisteret/regnskap/999888777").header("accept", "application/json"))
                .andExpect(status().is(500))
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.status", is("500")))
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.message", is("Regnskapet inneholder en oppstillingsplan som ikke er stottet (IKKESTOTTET)")))
                .andExpect(jsonPath("$.path", is("/regnskapsregisteret/regnskap/999888777")))
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.trace").isString());
    }

    @ParameterizedTest
    @MethodSource("getRegnskapByOrgnummerDefaultRDFTest_arguments")
    public void getRegnskapByOrgnummerDefaultRDFTest(Lang lang, String expectedResponseFile) throws IOException {
        when(httpServletRequestMock.getHeader("Accept")).thenReturn(lang.getHeaderString());
        ResponseEntity response = regnskapApiImpl.getRegnskap(httpServletRequestMock, "123456789", 2018, SELSKAP);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        String body = response.getBody() instanceof String
                ? (String) response.getBody()
                : new ObjectMapper().writeValueAsString(response.getBody());

        Model modelFromResponse = JenaResponseReader.parseResponse(body, lang.getName());
        Map<String, String> patches = new HashMap<>();
        patches.put("<identifier>", regnskap2018_1Id.toString());
        Model expectedResponse = JenaResponseReader.getExpectedResponse(expectedResponseFile, patches, lang.getName());

        boolean isIsomorphicWith = expectedResponse.isIsomorphicWith(modelFromResponse);
        if (!isIsomorphicWith) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                modelFromResponse.write(baos, lang.getName());
                String s = new String(baos.toByteArray(), StandardCharsets.UTF_8);
                LOGGER.info("Response differ. Got:\n" + s);
            }
        }

        assertTrue(isIsomorphicWith);
    }

    public static Stream<Arguments> getRegnskapByOrgnummerDefaultRDFTest_arguments() {
        return Stream.of(
                arguments(RDFLanguages.JSONLD, "regnskap/OrgnrResponseDefault.ld.json"),
                arguments(RDFLanguages.RDFXML, "regnskap/OrgnrResponseDefault.rdf.xml"),
                arguments(RDFLanguages.TURTLE, "regnskap/OrgnrResponseDefault.ttl")
        );
    }

    @ParameterizedTest
    @MethodSource("getRegnskapByIdDefaultTest_arguments")
    public void getRegnskapByIdDefaultTest(String acceptHeader, String expectedResponseFile) throws Exception {
        String expectedResponse = JenaResponseReader.resourceAsString(expectedResponseFile);
        mockMvc
                .perform(get("/regnskapsregisteret/regnskap/123456789/" + regnskap2018_1Id).header("accept", acceptHeader))
                .andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith(acceptHeader))
                .andExpect(content().string(expectedResponse));
    }

    public static Stream<Arguments> getRegnskapByIdDefaultTest_arguments() {
        return Stream.of(
                arguments("application/json", "regnskapById/IdResponseDefault.json"),
                arguments("application/rdf+json", "regnskapById/IdResponseDefault.rdf.json"),
                arguments("application/xml", "regnskapById/IdResponseDefault.xml")
        );
    }

    @ParameterizedTest
    @MethodSource("getRegnskapByIdDefaultRDFTest_arguments")
    public void getRegnskapByIdDefaultRDFTest(Lang lang, String expectedResponseFile) throws IOException {
        when(httpServletRequestMock.getHeader("Accept")).thenReturn(lang.getHeaderString());
        ResponseEntity response = regnskapApiImpl.getRegnskapById(httpServletRequestMock, "123456789", regnskap2018_1Id);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        String body = response.getBody() instanceof String
                ? (String) response.getBody()
                : new ObjectMapper().writeValueAsString(response.getBody());

        Model modelFromResponse = JenaResponseReader.parseResponse(body, lang.getName());
        Map<String, String> patches = new HashMap<>();
        patches.put("<identifier>", regnskap2018_1Id.toString());
        Model expectedResponse = JenaResponseReader.getExpectedResponse(expectedResponseFile, patches, lang.getName());

        boolean isIsomorphicWith = expectedResponse.isIsomorphicWith(modelFromResponse);
        if (!isIsomorphicWith) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                modelFromResponse.write(baos, lang.getName());
                String s = new String(baos.toByteArray(), StandardCharsets.UTF_8);
                LOGGER.info("Response differ. Got:\n" + s);
            }
        }

        assertTrue(isIsomorphicWith);
    }

    public static Stream<Arguments> getRegnskapByIdDefaultRDFTest_arguments() {
        return Stream.of(
                arguments(RDFLanguages.JSONLD, "regnskapById/IdResponseDefault.ld.json"),
                arguments(RDFLanguages.RDFXML, "regnskapById/IdResponseDefault.rdf.xml"),
                arguments(RDFLanguages.TURTLE, "regnskapById/IdResponseDefault.ttl")
        );
    }

    @Test
    public void getRegnskapByIdPartnerTurtleTest() throws IOException, SQLException {
        when(httpServletRequestMock.getHeader("Accept")).thenReturn("text/turtle");
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0"); // "Basic test:test"
        ResponseEntity response = regnskapApiImpl.getRegnskapById(httpServletRequestMock, "123456789", regnskap2018_1Id);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Model modelFromResponse = JenaResponseReader.parseResponse((String) response.getBody(), "TURTLE");
        Map<String, String> patches = new HashMap<>();
        patches.put("<identifier>", regnskap2018_1Id.toString());
        Model expectedResponse = JenaResponseReader.getExpectedResponse("regnskapById/IdResponsePartner.ttl", patches, "TURTLE");

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
        when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        ResponseEntity<Regnskap> response = regnskapApiImpl.getRegnskapById(httpServletRequestMock, "123456789", regnskap2018_1Id);
        Regnskap regnskap = response.getBody();

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
    void correctSumsInPersistenceFieldsPartnerSelskap() {
        when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0"); // "Basic test:test"
        ResponseEntity response = regnskapApiImpl.getRegnskap(httpServletRequestMock, "123456789", 2019, SELSKAP);
        List<Regnskap> body = (List<Regnskap>) response.getBody();
        assertEquals(1, body.size());
        Regnskap regnskap = body.get(0);

        long baseValue = 2019L;
        assertEquals(baseValue, regnskap.getEiendeler().getGoodwill().intValue());
        assertEquals(TestData.TEST_SELSKAP_VARER, regnskap.getEiendeler().getSumVarer().intValue());
        assertEquals(TestData.TEST_SELSKAP_FORDRINGER, regnskap.getEiendeler().getSumFordringer().intValue());
        assertNull(regnskap.getEiendeler().getSumInvesteringer());
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
        assertEquals(baseValue + 25, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().getSumDriftskostnad().intValue());
        assertEquals(baseValue + 26, regnskap.getResultatregnskapResultat().getFinansresultat().getNettoFinans().intValue());
        assertEquals(baseValue + 27, regnskap.getResultatregnskapResultat().getFinansresultat().getFinansinntekt().getSumFinansinntekter().intValue());
        assertEquals(baseValue + 28, regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getRentekostnadSammeKonsern().intValue());
        assertEquals(baseValue + 29, regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getAnnenRentekostnad().intValue());
        assertEquals(baseValue + 30, regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getSumFinanskostnad().intValue());
    }

    @Test
    void correctSumsInPersistenceFieldsPartnerKonsern() {
        when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/xml");
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0"); // "Basic test:test"
        ResponseEntity response = regnskapApiImpl.getRegnskap(httpServletRequestMock, "123456789", 2019, KONSERN);
        List<Regnskap> body = (List<Regnskap>) response.getBody();
        assertEquals(1, body.size());
        Regnskap regnskap = body.get(0);

        long baseValue = 2019L;
        assertEquals(baseValue, regnskap.getEiendeler().getGoodwill().intValue());
        assertEquals(TestData.TEST_KONSERN_VARER, regnskap.getEiendeler().getSumVarer().intValue());
        assertNull(regnskap.getEiendeler().getSumFordringer());
        assertEquals(TestData.TEST_KONSERN_INVESTERINGER, regnskap.getEiendeler().getSumInvesteringer().intValue());
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
