package no.brreg.regnskap.integration;


import kotlin.ranges.IntRange;
import no.brreg.regnskap.config.properties.AarsregnskapCopyProperties;
import no.brreg.regnskap.utils.EmbeddedPostgresSetup;
import no.brreg.regnskap.utils.stubs.ProcessStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static java.util.Objects.requireNonNull;
import static no.brreg.regnskap.config.CacheConfig.*;
import static no.brreg.regnskap.config.JdbcConfig.AARDB_DATASOURCE;
import static org.mockito.Mockito.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@Sql(
        executionPhase = BEFORE_TEST_METHOD,
        config = @SqlConfig(dataSource = AARDB_DATASOURCE),
        scripts = {"/sql_scripts/aardb.sql"}
)
public class MellombalanseControllerIT extends EmbeddedPostgresSetup {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Clock clock;

    @MockBean
    RestTemplate restTemplate;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    AarsregnskapCopyProperties  aarsregnskapCopyProperties;


    @BeforeEach
    void setUp() {
        Instant fixedInstant = Instant.parse("2029-01-01T00:00:00Z");
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

        requireNonNull(cacheManager.getCache(CACHE_MELLOMBALANSE_FILEMETA)).clear();
        requireNonNull(cacheManager.getCache(CACHE_AAR_REQUEST_BUCKET)).clear();
    }

    @Test
    public void getAvailableMellombalanse_shouldReturnExpectedResponse() throws Exception {
        mockMvc.perform(get("/regnskapsregisteret/regnskap/aarsregnskap/mellombalanse/310293903/aar"))
                .andExpect(status().isOk())
                .andExpect(content().string("[\"2014\",\"2022\",\"2023\",\"2024\",\"2025\"]"));
    }

    @Test
    public void getAvailableMellombalanse_shouldReturnExpectedResponseIfNoFiles() throws Exception {
        mockMvc.perform(get("/regnskapsregisteret/regnskap/aarsregnskap/mellombalanse/123456789/aar"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void getMellombalanse_shouldReturnCorrectHeaders() throws Exception {
        try (var mockProcessBuilder = mockConstruction(ProcessBuilder.class, (mock, context) -> {
            when(mock.redirectErrorStream(true)).thenReturn(mock);
            when(mock.start()).thenReturn(new ProcessStub(false));
        })) {
            mockMvc.perform(get("/regnskapsregisteret/regnskap/aarsregnskap/mellombalanse/310293903/2025"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", "application/pdf"))
                    .andExpect(header().string("Content-Disposition", "attachment; filename=mellombalanse-2025_310293903.pdf"));
        }
    }


    @Test
    public void getMellombalanse_shouldReturnCorrectHeaders_notInImageSys() throws Exception {
        when(restTemplate.execute(anyString(), eq(HttpMethod.GET), isNull(), any(ResponseExtractor.class))).thenAnswer(inv -> {
            ResponseExtractor<?> extractor = inv.getArgument(3);
            var mockResponse = mock(ClientHttpResponse.class);

            var mockFilePath = Paths.get(aarsregnskapCopyProperties.filepathPrefix(), "/AAR/MBAL/2025/00/00/26/2025000026.tif");
            try (var fis = Files.newInputStream(mockFilePath)) {
                when(mockResponse.getBody()).thenReturn(new ByteArrayInputStream(fis.readAllBytes()));
            }
            return extractor.extractData(mockResponse);
        });

        try (var mockProcessBuilder = mockConstruction(ProcessBuilder.class, (mock, context) -> {
            when(mock.redirectErrorStream(true)).thenReturn(mock);
            when(mock.start()).thenReturn(new ProcessStub(false));
        })) {
            mockMvc.perform(get("/regnskapsregisteret/regnskap/aarsregnskap/mellombalanse/310293903/2024"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", "application/pdf"))
                    .andExpect(header().string("Content-Disposition", "attachment; filename=mellombalanse-2024_310293903.pdf"));
        }
    }

    @Test
    public void getMellombalanse_shouldReturn404IfNoFile() throws Exception {
        mockMvc.perform(get("/regnskapsregisteret/regnskap/aarsregnskap/mellombalanse/310293903/2021"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void ratelimit_enforcedOnEndpoints() throws Exception {
        for (var i : new IntRange(0, 4)) {
            mockMvc.perform(get("/regnskapsregisteret/regnskap/aarsregnskap/mellombalanse/310293903/aar"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/regnskapsregisteret/regnskap/aarsregnskap/mellombalanse/310293903/aar"))
                .andExpect(status().isTooManyRequests());

        mockMvc.perform(get("/regnskapsregisteret/regnskap/aarsregnskap/mellombalanse/310293903/2022"))
                .andExpect(status().isTooManyRequests());
    }
}
