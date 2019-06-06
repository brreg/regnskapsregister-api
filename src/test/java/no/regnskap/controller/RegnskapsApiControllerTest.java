package no.regnskap.controller;

import no.regnskap.TestData;
import no.regnskap.generated.model.Regnskap;
import no.regnskap.service.RegnskapService;
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
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTest.class)
public class RegnskapsApiControllerTest {

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Mock
    RegnskapService regnskapServiceMock;

    @InjectMocks
    RegnskapsApiControllerImpl regnskapsApiController;

    @Before
    public void resetMocks() {
        Mockito.reset();
    }

    @Test
    public void noContentResponseWhenEmptyResult() {
        Optional<Regnskap> empty = Optional.empty();
        Mockito.when(regnskapServiceMock.getById("id"))
            .thenReturn(empty);

        ResponseEntity<Regnskap> response = regnskapsApiController.getRegnskapById(httpServletRequestMock, "id");

        Assert.assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
        Assert.assertNull(response.getBody());
    }

    @Test
    public void okResponseWhenNonEmptyResult() {
        Optional<Regnskap> regnskap = Optional.of(TestData.regnskap);
        Mockito.when(regnskapServiceMock.getById("id"))
            .thenReturn(regnskap);

        ResponseEntity<Regnskap> response = regnskapsApiController.getRegnskapById(httpServletRequestMock, "id");

        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assert.assertEquals(response.getBody(), TestData.regnskap);
    }
}
