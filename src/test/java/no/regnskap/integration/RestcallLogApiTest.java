package no.regnskap.integration;

import no.regnskap.model.RestcallLog;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@SpringBootTest
@ContextConfiguration(initializers = {RestcallLogApiTest.Initializer.class})
@Tag("service")
class RestcallLogApiTest extends TestContainersBase {
    private final static Logger LOGGER = LoggerFactory.getLogger(RestcallLogApiTest.class);


    List<RestcallLog> testData = Collections.unmodifiableList(Arrays.asList(
            new RestcallLog("10.0.0.1","getRegnskapById","123456789"),
            new RestcallLog("10.0.0.1","getRegnskap","234567890"),
            new RestcallLog("10.0.0.2","getRegnskap","123456789")));

    @Test
    public void test() {

    }
/*
    @Nested
    class GetStatisticsByIp {
        @Test
        void getStatisticsByIp() {
            LocalDate fraDato = LocalDate.of(2018, 1, 1);
            LocalDate tilDato = LocalDate.now();
            Mockito.when(restcallLogServiceMock.getLogEntries(fraDato, tilDato)).thenReturn(testData);

            List<String> logs  = restcallLogServiceMock.getStatisticsByIp(httpServletRequestMock, fraDato, tilDato);

            assertEquals(3, logs.size());
        }
    }

    @Nested
    class GetStatisticsByMethod {
        @Test
        void getStatisticsByMethod() {
            Mockito.when(regnskapServiceMock.getById("id"))
                .thenReturn(null);

            Mockito.when(valuesMock.regnskapsregisteretUrl()).thenReturn(TestData.rregUrl);
            Mockito.when(valuesMock.organizationCatalogueUrl()).thenReturn(TestData.orgcatUrl);

            ResponseEntity<Object> response = regnskapApi.getRegnskapById(httpServletRequestMock, "id");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }
    }

    @Nested
    class GetStatisticsByOrgnr {
        @Test
        void getStatisticsByOrgnr() {
            Mockito.when(regnskapServiceMock.getById("id"))
                .thenReturn(null);

            Mockito.when(valuesMock.regnskapsregisteretUrl()).thenReturn(TestData.rregUrl);
            Mockito.when(valuesMock.organizationCatalogueUrl()).thenReturn(TestData.orgcatUrl);

            ResponseEntity<Object> response = regnskapApi.getRegnskapById(httpServletRequestMock, "id");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }
    }
*/
}
