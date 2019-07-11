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

    @Mock
    UpdateService updateServiceMock;

    @InjectMocks
    RegnskapApiImpl regnskapApi;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(
            httpServletRequestMock,
            regnskapServiceMock,
            updateServiceMock
        );
    }

    @Nested
    class GetRegnskap {
        @Test
        void okWhenEmpty() {
            List<Regnskap> emptyList = TestData.EMPTY_REGNSKAP_LIST;
            Mockito.when(regnskapServiceMock.getByOrgnr("orgnummer"))
                .thenReturn(emptyList);

            ResponseEntity<List<Regnskap>> response = regnskapApi.getRegnskap(httpServletRequestMock, "orgnummer");

            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody(), TestData.EMPTY_REGNSKAP_LIST);
        }

        @Test
        void okWhenNonEmpty() {
            List<Regnskap> regnskapList = TestData.REGNSKAP_LIST;
            Mockito.when(regnskapServiceMock.getByOrgnr("orgnummer"))
                .thenReturn(regnskapList);

            ResponseEntity<List<Regnskap>> response = regnskapApi.getRegnskap(httpServletRequestMock, "orgnummer");

            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody(), TestData.REGNSKAP_LIST);
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

            assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
            assertNull(response.getBody());
        }

        @Test
        void okWhenNonEmpty() {
            Optional<Regnskap> regnskap = Optional.of(TestData.REGNSKAP_2018);
            Mockito.when(regnskapServiceMock.getById("id"))
                .thenReturn(regnskap);

            ResponseEntity<Regnskap> response = regnskapApi.getRegnskapById(httpServletRequestMock, "id");

            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody(), TestData.REGNSKAP_2018);
        }
    }
}
