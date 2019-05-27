package no.regnskap.controller;

import no.regnskap.TestData;
import no.regnskap.generated.model.Regnskap;
import no.regnskap.service.RegnskapService;
import no.regnskap.service.UpdateService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RegnskapApiControllerTest {

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Mock
    RegnskapService regnskapServiceMock;

    @Mock
    UpdateService updateServiceMock;

    @InjectMocks
    RegnskapApiControllerImpl regnskapApiController;

    @Before
    public void resetMocks() {
        Mockito.reset();
    }

    @Test
    public void okResponseWhenEmptyResult() {
        List<Regnskap> emptyList = TestData.emptyRegnskapList;
        Mockito.when(regnskapServiceMock.getByOrgnr("orgnummer"))
            .thenReturn(emptyList);

        ResponseEntity<List<Regnskap>> response = regnskapApiController.getRegnskap(httpServletRequestMock, "orgnummer");

        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assert.assertEquals(response.getBody(), TestData.emptyRegnskapList);
    }

    @Test
    public void okResponseWhenNonEmptyResult() {
        List<Regnskap> regnskapList = TestData.regnskapList;
        Mockito.when(regnskapServiceMock.getByOrgnr("orgnummer"))
            .thenReturn(regnskapList);

        ResponseEntity<List<Regnskap>> response = regnskapApiController.getRegnskap(httpServletRequestMock, "orgnummer");

        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assert.assertEquals(response.getBody(), TestData.regnskapList);
    }
}
