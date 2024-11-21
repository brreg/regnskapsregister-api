package no.brreg.regnskap.integration;


import no.brreg.regnskap.utils.EmbeddedPostgresSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static no.brreg.regnskap.config.JdbcConfig.AARDB_DATASOURCE;
import static org.mockito.Mockito.when;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@Sql(
        executionPhase = BEFORE_TEST_METHOD,
        config = @SqlConfig(dataSource = AARDB_DATASOURCE),
        scripts = {"/sql_scripts/aardb.sql"}
)
public class AarsregnskapControllerIT extends EmbeddedPostgresSetup {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Clock clock;


    @BeforeEach
    void setUp() {
        Instant fixedInstant = Instant.parse("2024-01-01T00:00:00Z");
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
    }

    @Test
    public void getAvailableAarsregnskap_shouldReturnExpectedResponse() throws Exception {
        mockMvc.perform(get("/aarsregnskap/312800640/aar"))
                .andExpect(status().isOk())
                .andExpect(content().string("[\"2014\",\"2022\",\"2023\"]"));
    }

    @Test
    public void getAvailableAarsregnskap_shouldReturnExpectedResponseIfNoFiles() throws Exception {
        mockMvc.perform(get("/aarsregnskap/123456789/aar"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void getAarsregnskapCopy_shouldReturnCorrectHeaders() throws Exception {
        mockMvc.perform(get("/aarsregnskap/kopi/312800640/2022"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=aarsregnskap-2022_312800640.pdf"));
    }

    @Test
    public void getAarsregnskapCopy_shouldReturn404IfNoFile() throws Exception {
        mockMvc.perform(get("/aarsregnskap/kopi/312800640/2024"))
                .andExpect(status().isNotFound());
    }
}
