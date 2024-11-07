package no.brreg.regnskap.integration;


import no.brreg.regnskap.utils.EmbeddedPostgresSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
public class AarsregnskapApiIT extends EmbeddedPostgresSetup {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAvailableAarsregnskap_shouldReturnExpectedResponse() throws Exception {
        mockMvc.perform(get("/aarsregnskap/987654321/aar"))
                .andExpect(status().isOk())
                .andExpect(content().string("[\"2024\"]"));
    }


    @Test
    public void getAarsregnskapCopy_shouldReturnCorrectHeaders() throws Exception {
        mockMvc.perform(get("/aarsregnskap/kopi/987654321/2024"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=aarsregnskap-2024_987654321.pdf"));
    }
}
