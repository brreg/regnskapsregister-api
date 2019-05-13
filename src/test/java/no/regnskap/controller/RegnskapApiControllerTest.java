package no.regnskap.controller;

import no.regnskap.generated.model.Regnskap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RunWith(SpringRunner.class)
public class RegnskapApiControllerTest {

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Test
    public void happyDay()
    {
        Assert.assertTrue(true);
    }

    @Test
    public void getRegnskapTest() {
        RegnskapApiControllerImpl invoicesApiController = new RegnskapApiControllerImpl();
        ResponseEntity<List<Regnskap>> response = invoicesApiController.getRegnskap(httpServletRequestMock, "123");
        Assert.assertNotEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
    }
}