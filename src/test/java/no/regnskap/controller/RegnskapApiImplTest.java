package no.regnskap.controller;

import no.regnskap.TestData;
import no.regnskap.configuration.ProfileConditionalValues;
import no.regnskap.generated.model.Regnskap;
import no.regnskap.service.RegnskapService;
import org.apache.jena.riot.RIOT;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
@Tag("unit")
class RegnskapApiImplTest {

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Mock
    RegnskapService regnskapServiceMock;

    @Mock
    ProfileConditionalValues valuesMock;

    @InjectMocks
    RegnskapApiImpl regnskapApi;

    @BeforeAll
    static void init() {
        RIOT.init();
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(
            httpServletRequestMock,
            regnskapServiceMock,
            valuesMock
        );
    }

    @Test
    void ready() {
        HttpStatus responseStatus = regnskapApi.getReady().getStatusCode();
        assertEquals(HttpStatus.OK, responseStatus);
    }

    @Nested
    class GetRegnskap {
        @Test
        void okWhenEmpty() {
            List<Regnskap> emptyList = TestData.EMPTY_REGNSKAP_LIST;
            Mockito.when(regnskapServiceMock.getByOrgnr("orgnummer", null, null))
                .thenReturn(emptyList);

            Mockito.when(valuesMock.regnskapsregisteretUrl()).thenReturn(TestData.rregUrl);
            Mockito.when(valuesMock.organizationCatalogueUrl()).thenReturn(TestData.orgcatUrl);

            Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/json");

            ResponseEntity<Object> response = regnskapApi.getRegnskap(httpServletRequestMock, "orgnummer", null, null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(TestData.EMPTY_REGNSKAP_LIST, response.getBody());
        }

        @Test
        void okWhenNonEmpty() {
            List<Regnskap> regnskapList = TestData.REGNSKAP_LIST;
            Mockito.when(regnskapServiceMock.getByOrgnr("orgnummer", null, null))
                .thenReturn(regnskapList);

            Mockito.when(valuesMock.regnskapsregisteretUrl()).thenReturn(TestData.rregUrl);
            Mockito.when(valuesMock.organizationCatalogueUrl()).thenReturn(TestData.orgcatUrl);

            Mockito.when(httpServletRequestMock.getHeader("Accept")).thenReturn("application/json");

            ResponseEntity<Object> response = regnskapApi.getRegnskap(httpServletRequestMock, "orgnummer", null, null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(TestData.REGNSKAP_LIST, response.getBody());
        }

        @Test
        void unknownError() {
            Mockito.when(regnskapServiceMock.getByOrgnr("orgnummer", null, null))
                .thenAnswer(invocation -> {
                    throw new Exception("Test error message");
                });

            HttpStatus responseStatus = regnskapApi.getRegnskap(httpServletRequestMock, "orgnummer", null, null).getStatusCode();
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseStatus);
        }
    }

    @Nested
    class GetRegnskapById {
        @Test
        void notFoundWhenEmpty() {
            Mockito.when(regnskapServiceMock.getById("id"))
                .thenReturn(null);

            Mockito.when(valuesMock.regnskapsregisteretUrl()).thenReturn(TestData.rregUrl);
            Mockito.when(valuesMock.organizationCatalogueUrl()).thenReturn(TestData.orgcatUrl);

            ResponseEntity<Object> response = regnskapApi.getRegnskapById(httpServletRequestMock, "id");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        void okWhenNonEmpty() {
            Regnskap regnskap = TestData.REGNSKAP_2018;
            Mockito.when(regnskapServiceMock.getById("id"))
                .thenReturn(regnskap);

            Mockito.when(valuesMock.regnskapsregisteretUrl()).thenReturn(TestData.rregUrl);
            Mockito.when(valuesMock.organizationCatalogueUrl()).thenReturn(TestData.orgcatUrl);

            ResponseEntity<Object> response = regnskapApi.getRegnskapById(httpServletRequestMock, "id");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(TestData.REGNSKAP_2018, response.getBody());
        }

        @Test
        void unknownError() {
            Mockito.when(regnskapServiceMock.getById("id"))
                .thenAnswer(invocation -> {
                    throw new Exception("Test error message");
                });

            HttpStatus responseStatus = regnskapApi.getRegnskapById(httpServletRequestMock, "id").getStatusCode();
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseStatus);
        }
    }

    @Nested
    class GetLog {
        @Test
        void answersWithList() {
            List<String> list = new ArrayList<>();
            list.add("entry.xml");
            list.add("anotherEntry.xml");

            Mockito.when(regnskapServiceMock.getLog())
                .thenReturn(list);

            ResponseEntity<List<String>> response = regnskapApi.getLog(httpServletRequestMock);

            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody(), list);
        }
    }
}
