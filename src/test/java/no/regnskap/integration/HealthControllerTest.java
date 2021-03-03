package no.regnskap.integration;

import no.regnskap.controller.HealthController;
import no.regnskap.utils.TestContainersBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jubiter.api.Disabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("Disabled until TestContainer support have been added to Jenkins slave")
@SpringBootTest
@ContextConfiguration(initializers = {TestContainersBase.Initializer.class})
@Tag("service")
public class HealthControllerTest extends TestContainersBase {
    private final static Logger LOGGER = LoggerFactory.getLogger(HealthControllerTest.class);

    @Autowired
    private HealthController healthController;


    @Test
    public void getPingTest() {
        ResponseEntity<String> response = healthController.getPing();
        assertEquals("pong", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getReadyTest() {
        ResponseEntity<Void> response = healthController.getReady();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
