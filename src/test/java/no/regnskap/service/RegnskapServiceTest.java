package no.regnskap.service;

import no.regnskap.TestData;
import no.regnskap.generated.model.Regnskap;
import no.regnskap.model.RegnskapDB;
import no.regnskap.model.RegnskapLog;
import no.regnskap.repository.RegnskapLogRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
@Tag("unit")
class RegnskapServiceTest {

    @Mock
    RegnskapRepository repositoryMock;

    @Mock
    RegnskapLogRepository logRepositoryMock;

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

            Regnskap actualResult = regnskapService.getById(TestData.GENERATED_ID_0.toHexString());

            assertEquals(null, actualResult);
        }

        @Test
        void mappingIsCorrect() {
            Optional<RegnskapDB> regnskapDB = Optional.of(TestData.DB_REGNSKAP_2018_SECOND);
            Mockito.when(repositoryMock.findById(TestData.GENERATED_ID_2.toHexString()))
                .thenReturn(regnskapDB);

            Regnskap actual = regnskapService.getById(TestData.GENERATED_ID_2.toHexString());

            assertEquals(TestData.REGNSKAP_2018, actual);
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
            assertEquals(TestData.REGNSKAP_LIST, actual);
        }

        @Test
        void filtersWork() {
            List<RegnskapDB> list = new ArrayList<>();

            RegnskapDB db0 = TestData.createRegnskapDB(TestData.GENERATED_ID_0, 2019,"0");
            db0.setAarsregnskapstype("SMAA");
            list.add(db0);
            RegnskapDB db1 = TestData.createRegnskapDB(TestData.GENERATED_ID_1, 2019,"1");
            db1.setRegnskapstype("K");
            list.add(db1);

            Mockito.when(repositoryMock.findByOrgnrOrderByJournalnrDesc("orgnummer"))
                .thenReturn(list);
            List<Regnskap> actual = regnskapService.getByOrgnr("orgnummer");

            assertTrue(actual.isEmpty());
        }
    }

    @Nested
    class Log {
        @Test
        void emptyListWhenNoEntriesInDB() {
            List<RegnskapLog> emptyList = new ArrayList<>();
            Mockito.when(logRepositoryMock.findAll(TestData.DB_SORT))
                .thenReturn(emptyList);

            List<String> result = regnskapService.getLog();

            assertTrue(result.isEmpty());
        }

        @Test
        void allEntriesIncluded() {
            List<RegnskapLog> list = TestData.DB_LOG;
            Mockito.when(logRepositoryMock.findAll(TestData.DB_SORT))
                .thenReturn(list);

            List<String> result = regnskapService.getLog();

            assertEquals(result.size(), 4);
            assertTrue(result.contains("file0.xml"));
            assertTrue(result.contains("file1.xml"));
            assertTrue(result.contains("file2.xml"));
            assertTrue(result.contains("file3.xml"));
        }
    }
}
