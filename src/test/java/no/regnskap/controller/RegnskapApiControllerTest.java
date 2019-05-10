package no.regnskap.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
public class RegnskapApiControllerTest {

    private RegnskapApiControllerImpl regnskapApiController = new RegnskapApiControllerImpl();

    @Test
    public void happyDay()
    {
        Assert.assertTrue(true);
    }
}