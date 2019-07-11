package no.regnskap.service;

import no.regnskap.TestData;
import no.regnskap.generated.model.Regnskap;
import no.regnskap.model.RegnskapDB;
import no.regnskap.repository.RegnskapRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
@Tag("unit")
class RegnskapServiceTest {

    @Mock
    RegnskapRepository repositoryMock;

    @InjectMocks
    RegnskapService regnskapService;

    @BeforeEach
    void resetMocks() {
        Mockito.reset();
    }

    @Nested
    class FindById {
        @Test
        void emptyOptionalWhenNotFoundInDB() {
            Optional<RegnskapDB> emptyDB = Optional.empty();
            Mockito.when(repositoryMock.findById(TestData.GENERATED_ID_0.toHexString()))
                .thenReturn(emptyDB);

            Optional<Regnskap> actualResult = regnskapService.getById(TestData.GENERATED_ID_0.toHexString());
            Optional<Regnskap> emptyResult = Optional.empty();

            assertEquals(actualResult, emptyResult);
        }

        @Test
        void mappingIsCorrect() {
            Optional<RegnskapDB> regnskapDB = Optional.of(TestData.DB_REGNSKAP_2018_SECOND);
            Mockito.when(repositoryMock.findById(TestData.GENERATED_ID_2.toHexString()))
                .thenReturn(regnskapDB);

            Optional<Regnskap> actual = regnskapService.getById(TestData.GENERATED_ID_2.toHexString());

            assertTrue(actual.isPresent());
            assertEquals(actual.get(), TestData.REGNSKAP_2018);
        }
    }

    @Nested
    class FindByOrgnr {
        @Test
        void emptyListWhenNotFoundInDB() {
            List<RegnskapDB> emptyDatabaseList = TestData.EMPTY_DB_REGNSKAP_LIST;
            Mockito.when(repositoryMock.findByOrgnrOrderByJournalnrDesc("orgnummer"))
                .thenReturn(emptyDatabaseList);

            List<Regnskap> result = regnskapService.getByOrgnr("orgnummer");

            assertTrue(result.isEmpty());
        }

        @Test
        void mappingIsCorrect() {
            List<RegnskapDB> dbList = TestData.DB_REGNSKAP_LIST;
            Mockito.when(repositoryMock.findByOrgnrOrderByJournalnrDesc("orgnummer"))
                .thenReturn(dbList);

            List<Regnskap> actual = regnskapService.getByOrgnr("orgnummer");

            assertFalse(actual.isEmpty());
            assertEquals(actual, TestData.REGNSKAP_LIST);
        }
    }
}
