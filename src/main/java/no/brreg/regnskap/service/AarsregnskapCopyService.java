package no.brreg.regnskap.service;

import no.brreg.regnskap.controller.exception.InternalServerError;
import no.brreg.regnskap.repository.AarsregnskapRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Clock;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
public class AarsregnskapCopyService {

    private final PdfConverterService pdfConverterService;
    private final AarsregnskapRepository aarsregnskapRepository;
    private final Clock clock;

    @Value("classpath:aarsregnskap-mock-files/example-multipage.tiff")
    private Resource mockFileResource;

    public AarsregnskapCopyService(PdfConverterService pdfConverterService, AarsregnskapRepository aarsregnskapRepository, Clock clock) {
        this.pdfConverterService = pdfConverterService;
        this.aarsregnskapRepository = aarsregnskapRepository;
        this.clock = clock;

    }

    public List<String> getAvailableAarsregnskapYears(String orgnr) {
        var years = aarsregnskapRepository.getAvailableAarsregnskap(orgnr);
        Year tenYearsAgo = Year.now(clock).minusYears(10);

        return years.stream()
                .filter(y -> !Year.parse(y).isBefore(tenYearsAgo))
                .toList();
    }

    public Optional<byte[]> getAarsregnskapCopy(String orgnr, String year) {
        var aarsregnskapPath = aarsregnskapRepository.getAarsregnskapPath(orgnr, year);
        if (aarsregnskapPath.orElse("").isEmpty()) {
            return Optional.empty();
        }

        String mockFilePath;
        try {
            mockFilePath = mockFileResource.getURI().getPath();
        } catch (IOException e) {
            throw new InternalServerError(e);
        }

        return Optional.of(pdfConverterService.tiffToPdf(mockFilePath));
    }
}
