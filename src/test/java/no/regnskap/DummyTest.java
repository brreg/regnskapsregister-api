package no.regnskap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DummyTest {
    //Dummy test to ensure that test report generation works in build process

    @Test
    public void neverFailingTest() {
        Assertions.assertEquals(1,1);
    }
}