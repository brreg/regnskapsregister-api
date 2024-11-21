package no.brreg.regnskap.configuration;

import no.brreg.regnskap.Application;
import no.brreg.regnskap.repository.AarsregnskapH2Repository;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({Application.class, AarsregnskapH2Repository.class})
public class TestConfig {
}
