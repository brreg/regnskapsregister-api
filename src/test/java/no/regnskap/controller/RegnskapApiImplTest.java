package no.regnskap.controller;

import no.regnskap.TestData;
import no.regnskap.generated.model.Regnskap;
import no.regnskap.service.RegnskapService;
import no.regnskap.service.UpdateService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTest.class)
public class RegnskapApiImplTest {

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Mock
    RegnskapService regnskapServiceMock;

    @Mock
    UpdateService updateServiceMock;

    @InjectMocks
    RegnskapApiImpl regnskapApi;

    @Before
    public void resetMocks() {
        Mockito.reset(
            httpServletRequestMock,
            regnskapServiceMock,
            updateServiceMock
        );
    }

    @Test
    public void getRegnskapOkWhenEmpty() {
        List<Regnskap> emptyList = TestData.emptyRegnskapList;
        Mockito.when(regnskapServiceMock.getByOrgnr("orgnummer"))
            .thenReturn(emptyList);

        ResponseEntity<List<Regnskap>> response = regnskapApi.getRegnskap(httpServletRequestMock, "orgnummer");

        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assert.assertEquals(response.getBody(), TestData.emptyRegnskapList);
    }

    @Test
    public void getRegnskapOkWhenNonEmpty() {
        List<Regnskap> regnskapList = TestData.regnskapList;
        Mockito.when(regnskapServiceMock.getByOrgnr("orgnummer"))
            .thenReturn(regnskapList);

        ResponseEntity<List<Regnskap>> response = regnskapApi.getRegnskap(httpServletRequestMock, "orgnummer");

        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assert.assertEquals(response.getBody(), TestData.regnskapList);
    }

    @Test
    public void getRegnskapByIdNotFoundWhenEmpty() {
        Optional<Regnskap> empty = Optional.empty();
        Mockito.when(regnskapServiceMock.getById("id"))
            .thenReturn(empty);

        ResponseEntity<Regnskap> response = regnskapApi.getRegnskapById(httpServletRequestMock, "id");

        Assert.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        Assert.assertNull(response.getBody());
    }

    @Test
    public void getRegnskapByIdOkWhenNonEmpty() {
        Optional<Regnskap> regnskap = Optional.of(TestData.regnskap);
        Mockito.when(regnskapServiceMock.getById("id"))
            .thenReturn(regnskap);

        ResponseEntity<Regnskap> response = regnskapApi.getRegnskapById(httpServletRequestMock, "id");

        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assert.assertEquals(response.getBody(), TestData.regnskap);
    }
}
