package no.brreg.regnskap.service;

import no.brreg.regnskap.config.properties.AarsregnskapCopyProperties;
import no.brreg.regnskap.model.AarsregnskapFileMeta;
import no.brreg.regnskap.repository.AarsregnskapRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.time.Clock;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
public class AarsregnskapCopyService {

    private final AarsregnskapCopyProperties aarsregnskapCopyProperties;
    private final PdfConverterService pdfConverterService;
    private final AarsregnskapRepository aarsregnskapRepository;
    private final Clock clock;

    public AarsregnskapCopyService(AarsregnskapCopyProperties aarsregnskapCopyProperties, PdfConverterService pdfConverterService, AarsregnskapRepository aarsregnskapRepository, Clock clock) {
        this.aarsregnskapCopyProperties = aarsregnskapCopyProperties;
        this.pdfConverterService = pdfConverterService;
        this.aarsregnskapRepository = aarsregnskapRepository;
        this.clock = clock;
    }

    public List<String> getAvailableAarsregnskapYears(String orgnr) {
        Year tenYearsAgo = Year.now(clock).minusYears(10);

        return aarsregnskapRepository.getAarsregnskapMeta(orgnr).stream()
                .map(AarsregnskapFileMeta::regnaar)
                .filter(y -> !Year.parse(y).isBefore(tenYearsAgo))
                .toList();
    }

    public Optional<byte[]> getAarsregnskapCopy(String orgnr, String year) {
        return aarsregnskapRepository.getAarsregnskapMeta(orgnr).stream()
                .filter(meta -> meta.regnaar().equals(year))
                .findFirst()
                .map(AarsregnskapFileMeta::path)
                .map(FilenameUtils::separatorsToSystem)
                .map(path -> Paths.get(aarsregnskapCopyProperties.filepathPrefix(), path).toString())
                .map(pdfConverterService::tiffToPdf);

    }
}
