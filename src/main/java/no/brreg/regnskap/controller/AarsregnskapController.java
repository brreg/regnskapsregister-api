package no.brreg.regnskap.controller;

import no.brreg.regnskap.service.PdfConverterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import static java.util.Collections.singletonList;


@ConditionalOnProperty("regnskap.aarsregnskap-copy.enabled")
@Controller
public class AarsregnskapController {

    private final PdfConverterService pdfConverterService;

    @Value("${TEST_FILE_PATH:./src/test/resources/aarsregnskap-files/example-multipage.tiff}")
    private String testFilePath;

    public AarsregnskapController(PdfConverterService pdfConverterService) {
        this.pdfConverterService = pdfConverterService;
    }


    @GetMapping("/aarsregnskap/{orgnr}/aar")
    public ResponseEntity<List<String>> getAvailableAarsregnskap(@PathVariable String orgnr) {
        return ResponseEntity
                .status(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body(singletonList("2024"));
    }

    @GetMapping("/aarsregnskap/kopi/{orgnr}/{aar}")
    public ResponseEntity<byte[]> getAarsregnskapCopy(@PathVariable("orgnr") String orgnr,
                                                      @PathVariable("aar") String aar) {

        var pdf = this.pdfConverterService.tiffToPdf(testFilePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set("Content-disposition", "attachment; filename=aarsregnskap-%s_%s.pdf".formatted(aar, orgnr));

        return ResponseEntity
                .status(200)
                .headers(headers)
                .body(pdf);
    }
}
