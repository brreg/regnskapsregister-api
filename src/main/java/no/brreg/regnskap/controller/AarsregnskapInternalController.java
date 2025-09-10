package no.brreg.regnskap.controller;

import no.brreg.regnskap.controller.exception.InternalServerError;
import no.brreg.regnskap.service.AarsregnskapCopyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@ConditionalOnProperty("regnskap.aarsregnskap-copy.enabled")
@Controller
public class AarsregnskapInternalController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AarsregnskapInternalController.class);

    private final AarsregnskapCopyService aarsregnskapCopyService;


    public AarsregnskapInternalController(AarsregnskapCopyService aarsregnskapCopyService) {
        this.aarsregnskapCopyService = aarsregnskapCopyService;
    }

    @GetMapping("/aarsregnskap/{orgnr}/aar")
    public ResponseEntity<List<String>> getAvailableAarsregnskap(@PathVariable String orgnr) {
        var yearList = this.aarsregnskapCopyService.getAvailableAarsregnskapYears(orgnr);

        return ResponseEntity
                .status(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body(yearList);
    }

    @GetMapping("/aarsregnskap/{orgnr}/kopi/{aar}")
    public ResponseEntity<byte[]> getAarsregnskapCopy(@PathVariable("orgnr") String orgnr,
                                                      @PathVariable("aar") String aar) {

        var aarsregnskapPdf = this.aarsregnskapCopyService.getAarsregnskapCopy(orgnr, aar);

        if (aarsregnskapPdf.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set("Content-disposition", "attachment; filename=aarsregnskap-%s_%s.pdf".formatted(aar, orgnr));

        return ResponseEntity
                .status(200)
                .headers(headers)
                .body(aarsregnskapPdf.get());
    }

    @GetMapping("/aarsregnskap/baerekraft/{orgnr}/aar")
    public ResponseEntity<List<String>> getAvailableBaerekraft(@PathVariable String orgnr) {
        var yearList = this.aarsregnskapCopyService.getAvailableBaerekraftYears(orgnr);

        return ResponseEntity
                .status(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body(yearList);
    }

    @GetMapping("/aarsregnskap/baerekraft/{orgnr}/file/{aar}")
    public ResponseEntity<StreamingResponseBody> getBaerekraftCopy(@PathVariable("orgnr") String orgnr,
                                                                   @PathVariable("aar") String aar) {

        var baerekraftFile = this.aarsregnskapCopyService.getBaerekraftrapport(orgnr, aar);

        if (baerekraftFile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        headers.set("Content-disposition", "attachment; filename=baerekraft-%s_%s.zip".formatted(aar, orgnr));

        return new ResponseEntity<>((outputStream) -> {
            try (var fileStream = new FileInputStream(baerekraftFile.get())) {
                fileStream.transferTo(outputStream);
            } catch (IOException e) {
                LOGGER.error("Failed streaming baerekraft ZIP-file", e);
                throw new InternalServerError(e);
            }
        }, headers, HttpStatus.OK);
    }
}
