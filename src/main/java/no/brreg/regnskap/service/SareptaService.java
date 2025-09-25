package no.brreg.regnskap.service;

import no.brreg.regnskap.config.properties.AarsregnskapCopyProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.Optional;

@Service
public class SareptaService {
    private final RestTemplate restTemplate;
    private final AarsregnskapCopyProperties aarsregnskapCopyProperties;
    private final PdfConverterService pdfConverterService;

    public SareptaService(RestTemplate restTemplate, AarsregnskapCopyProperties aarsregnskapCopyProperties, PdfConverterService pdfConverterService) {
        this.restTemplate = restTemplate;
        this.aarsregnskapCopyProperties = aarsregnskapCopyProperties;
        this.pdfConverterService = pdfConverterService;
    }

    public Optional<byte[]> getMellombalanse(String path) {
        var uri = this.aarsregnskapCopyProperties.sareptaUrl() + "mellombalanse" + path;
        return restTemplate.execute(uri, HttpMethod.GET, null, res -> {
            try (InputStream inputStream = res.getBody()) {
                return Optional.of(pdfConverterService.tiffToPdf(inputStream));
            }
        });
    }
}
