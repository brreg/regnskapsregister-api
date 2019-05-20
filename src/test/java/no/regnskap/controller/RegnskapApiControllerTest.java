package no.regnskap.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;


@RunWith(SpringRunner.class)
public class RegnskapApiControllerTest {

    @Mock
    HttpServletRequest httpServletRequestMock;

    @Test
    public void happyDay()
    {
        Assert.assertTrue(true);
    }
}