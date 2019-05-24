package no.regnskap.service;

import no.regnskap.TestData;
import no.regnskap.generated.model.Regnskap;
import no.regnskap.model.RegnskapDB;
import no.regnskap.repository.RegnskapRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
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
        Mockito.when(repositoryMock.findById("id"))
            .thenReturn(emptyDB);

        Optional<Regnskap> actualResult = regnskapService.getById("id");
        Optional<Regnskap> emptyResult = Optional.empty();

        Assert.assertEquals(actualResult, emptyResult);
    }

    @Test
    public void databaseResultForIdIsMappedCorrectly() {
        Optional<RegnskapDB> regnskapDB = Optional.of(TestData.regnskapDB);
        Mockito.when(repositoryMock.findById("id"))
            .thenReturn(regnskapDB);

        Optional<Regnskap> actual = regnskapService.getById("id");
        Optional<Regnskap> expected = Optional.of(TestData.regnskap);

        Assert.assertTrue(actual.isPresent());
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void emptyDatabaseResultForOrgnrGivesEmptyList() {
        List<RegnskapDB> emptyDatabaseList = TestData.emptyDatabaseList;
        Mockito.when(repositoryMock.findByOrgnr("orgnummer"))
            .thenReturn(emptyDatabaseList);

        List<Regnskap> result = regnskapService.getByOrgnr("orgnummer");

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void databaseResultForOrgnrIsMappedCorrectly() {
        List<RegnskapDB> dbList = TestData.databaseList;
        Mockito.when(repositoryMock.findByOrgnr("orgnummer"))
            .thenReturn(dbList);

        List<Regnskap> actual = regnskapService.getByOrgnr("orgnummer");
        List<Regnskap> expected = TestData.regnskapList;

        Assert.assertFalse(actual.isEmpty());
        Assert.assertEquals(actual, expected);
    }
}
