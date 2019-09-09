package no.regnskap.controller;

import no.regnskap.TestData;
import no.regnskap.generated.model.Regnskap;
import no.regnskap.service.RegnskapService;
import no.regnskap.service.UpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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
import java.util.Optional;

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

    @InjectMocks
    RegnskapApiImpl regnskapApi;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(
            httpServletRequestMock,
            regnskapServiceMock
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
            Mockito.when(regnskapServiceMock.getByOrgnr("orgnummer"))
                .thenReturn(emptyList);

            ResponseEntity<List<Regnskap>> response = regnskapApi.getRegnskap(httpServletRequestMock, "orgnummer");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(TestData.EMPTY_REGNSKAP_LIST, response.getBody());
        }

        @Test
        void okWhenNonEmpty() {
            List<Regnskap> regnskapList = TestData.REGNSKAP_LIST;
            Mockito.when(regnskapServiceMock.getByOrgnr("orgnummer"))
                .thenReturn(regnskapList);

            ResponseEntity<List<Regnskap>> response = regnskapApi.getRegnskap(httpServletRequestMock, "orgnummer");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(TestData.REGNSKAP_LIST, response.getBody());
        }

        @Test
        void unknownError() {
            Mockito.when(regnskapServiceMock.getByOrgnr("orgnummer"))
                .thenAnswer(invocation -> {
                    throw new Exception("Test error message");
                });

            HttpStatus responseStatus = regnskapApi.getRegnskap(httpServletRequestMock, "orgnummer").getStatusCode();
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseStatus);
        }
    }

    @Nested
    class GetRegnskapById {
        @Test
        void notFoundWhenEmpty() {
            Optional<Regnskap> empty = Optional.empty();
            Mockito.when(regnskapServiceMock.getById("id"))
                .thenReturn(empty);

            ResponseEntity<Regnskap> response = regnskapApi.getRegnskapById(httpServletRequestMock, "id");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        void okWhenNonEmpty() {
            Optional<Regnskap> regnskap = Optional.of(TestData.REGNSKAP_2018);
            Mockito.when(regnskapServiceMock.getById("id"))
                .thenReturn(regnskap);

            ResponseEntity<Regnskap> response = regnskapApi.getRegnskapById(httpServletRequestMock, "id");

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
