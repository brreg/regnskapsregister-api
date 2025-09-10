package no.brreg.regnskap.service;

import no.brreg.regnskap.config.properties.AarsregnskapCopyProperties;
import no.brreg.regnskap.model.AarsregnskapFileMeta;
import no.brreg.regnskap.repository.AarsregnskapRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnProperty("regnskap.aarsregnskap-copy.enabled")
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
        return fileMetaToAvailableYears(aarsregnskapRepository.getAarsregnskapMeta(orgnr));
    }

    public Optional<byte[]> getAarsregnskapCopy(String orgnr, String year) {
        return getFilepath(aarsregnskapRepository.getAarsregnskapMeta(orgnr), year)
                .map(path -> Paths.get(aarsregnskapCopyProperties.filepathPrefix(), path).toString())
                .map(pdfConverterService::tiffToPdf);
    }

    public List<String> getAvailableMellombalanseYears(String orgnr) {
        return fileMetaToAvailableYears(aarsregnskapRepository.getMellombalanseMeta(orgnr));
    }

    public Optional<byte[]> getMellombalanse(String orgnr, String year) {
        return getFilepath(aarsregnskapRepository.getMellombalanseMeta(orgnr), year)
                .map(path -> Paths.get(aarsregnskapCopyProperties.filepathPrefix(), "AAR/MBAL", path).toString())
                .map(path -> path.replace(".tif", ".pdf"))
                .map(path -> {
                    try (
                            var fis = new FileInputStream(path);
                            var baos = new ByteArrayOutputStream();
                        )
                    {
                        fis.transferTo(baos);
                        return baos.toByteArray();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public List<String> getAvailableBaerekraftYears(String orgnr) {
        return fileMetaToAvailableYears(aarsregnskapRepository.getBaerekraftMeta(orgnr));
    }

    public Optional<File> getBaerekraftrapport(String orgnr, String year) {
        return getFilepath(aarsregnskapRepository.getBaerekraftMeta(orgnr), year)
                .map(path -> Paths.get(aarsregnskapCopyProperties.filepathPrefix(), path).toString())
                .map(File::new);
    }


    private List<String> fileMetaToAvailableYears(List<AarsregnskapFileMeta> meta) {
        Year yearLimit = Year.now(clock).minusYears(this.aarsregnskapCopyProperties.yearsAvailable());
        return meta.stream()
                .map(AarsregnskapFileMeta::regnaar)
                .filter(y -> !Year.parse(y).isBefore(yearLimit))
                .toList();
    }

    private Optional<String> getFilepath(List<AarsregnskapFileMeta> metadata, String year) {
        return metadata.stream()
                .filter(meta -> meta.regnaar().equals(year))
                .findFirst()
                .map(AarsregnskapFileMeta::path)
                .map(FilenameUtils::separatorsToSystem);

    }
}
