package no.brreg.regnskap.service;

import no.brreg.regnskap.utils.EmbeddedPostgresSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        properties = "regnskap.update.enabled=true"
)
class UpdateServiceEnabledIT extends EmbeddedPostgresSetup {

    @Autowired
    private ConfigurableApplicationContext context;

    @Test
    void updateService_enabled_hasEnsureIndicesOnStartupService() {
        AssertableApplicationContext testContext = AssertableApplicationContext.get(() -> context);
        assertThat(testContext).hasSingleBean(UpdateService.class);
    }
}