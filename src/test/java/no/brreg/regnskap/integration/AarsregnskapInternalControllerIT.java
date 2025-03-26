package no.brreg.regnskap.integration;


import kotlin.ranges.IntRange;
import no.brreg.regnskap.utils.EmbeddedPostgresSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static java.util.Objects.requireNonNull;
import static no.brreg.regnskap.config.CacheConfig.CACHE_AAR_COPY_FILEMETA;
import static no.brreg.regnskap.config.CacheConfig.CACHE_AAR_REQUEST_BUCKET;
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
public class AarsregnskapInternalControllerIT extends EmbeddedPostgresSetup {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Clock clock;

    @Autowired
    CacheManager cacheManager;


    @BeforeEach
    void setUp() {
        Instant fixedInstant = Instant.parse("2029-01-01T00:00:00Z");
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

        requireNonNull(cacheManager.getCache(CACHE_AAR_COPY_FILEMETA)).clear();
        requireNonNull(cacheManager.getCache(CACHE_AAR_REQUEST_BUCKET)).clear();
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
        mockMvc.perform(get("/aarsregnskap/312800640/kopi/2022"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=aarsregnskap-2022_312800640.pdf"));
    }

    @Test
    public void getAarsregnskapCopy_shouldReturn404IfNoFile() throws Exception {
        mockMvc.perform(get("/aarsregnskap/312800640/kopi/2024"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void getAvailableBaerekraftrapport_shouldReturnExpectedResponse() throws Exception {
        mockMvc.perform(get("/aarsregnskap/baerekraft/310293903/aar"))
                .andExpect(status().isOk())
                .andExpect(content().string("[\"2014\",\"2022\",\"2023\"]"));
    }

    @Test
    public void getAvailableBaerekraftrapport_shouldReturnExpectedResponseIfNoFiles() throws Exception {
        mockMvc.perform(get("/aarsregnskap/baerekraft/123456789/aar"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void getBaerekraftrapport_shouldReturnCorrectHeaders() throws Exception {
        mockMvc.perform(get("/aarsregnskap/baerekraft/310293903/file/2022"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/zip"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=baerekraft-2022_310293903.zip"));
    }

    @Test
    public void getBaerekraftrapport_shouldReturn404IfNoFile() throws Exception {
        mockMvc.perform(get("/aarsregnskap/baerekraft/310293903/file/2024"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void ratelimit_enforcedOnEndpoints() throws Exception {
        for (var i : new IntRange(0, 4)) {
            mockMvc.perform(get("/aarsregnskap/312800640/aar"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/aarsregnskap/312800640/aar"))
                .andExpect(status().isTooManyRequests());

        mockMvc.perform(get("/aarsregnskap/312800640/kopi/2022"))
                .andExpect(status().isTooManyRequests());
    }
}
