package no.regnskap.service;

import no.regnskap.TestData;
import no.regnskap.generated.model.Regnskap;
import no.regnskap.model.RegnskapDB;
import no.regnskap.repository.RegnskapRepository;
import no.regnskap.testcategories.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTest.class)
public class RegnskapServiceTest {

    @Mock
    RegnskapRepository repositoryMock;

    @InjectMocks
    RegnskapService regnskapService;

    @Before
    public void resetMocks() {
        Mockito.reset();
    }

    @Test
    public void emptyDatabaseResultForIdGivesEmptyOptional() {
        Optional<RegnskapDB> emptyDB = Optional.empty();
        Mockito.when(repositoryMock.findById(TestData.GENERATED_ID_0.toHexString()))
            .thenReturn(emptyDB);

        Optional<Regnskap> actualResult = regnskapService.getById(TestData.GENERATED_ID_0.toHexString());
        Optional<Regnskap> emptyResult = Optional.empty();

        Assert.assertEquals(actualResult, emptyResult);
    }

    @Test
    public void databaseResultForIdIsMappedCorrectly() {
        Optional<RegnskapDB> regnskapDB = Optional.of(TestData.regnskap2018Second);
        Mockito.when(repositoryMock.findById(TestData.GENERATED_ID_2.toHexString()))
            .thenReturn(regnskapDB);

        Optional<Regnskap> actual = regnskapService.getById(TestData.GENERATED_ID_2.toHexString());

        Assert.assertTrue(actual.isPresent());
        Assert.assertEquals(actual.get(), TestData.regnskap);
    }

    @Test
    public void emptyDatabaseResultForOrgnrGivesEmptyList() {
        List<RegnskapDB> emptyDatabaseList = TestData.emptyDatabaseList;
        Mockito.when(repositoryMock.findByOrgnrOrderByJournalnrDesc("orgnummer"))
            .thenReturn(emptyDatabaseList);

        List<Regnskap> result = regnskapService.getByOrgnr("orgnummer");

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void databaseResultForOrgnrIsMappedCorrectly() {
        List<RegnskapDB> dbList = TestData.databaseList;
        Mockito.when(repositoryMock.findByOrgnrOrderByJournalnrDesc("orgnummer"))
            .thenReturn(dbList);

        List<Regnskap> actual = regnskapService.getByOrgnr("orgnummer");

        Assert.assertFalse(actual.isEmpty());
        Assert.assertEquals(actual, TestData.regnskapList);
    }
}
