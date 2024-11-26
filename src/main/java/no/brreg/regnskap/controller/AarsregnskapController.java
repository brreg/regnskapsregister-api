package no.brreg.regnskap.controller;

import no.brreg.regnskap.service.AarsregnskapCopyService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@ConditionalOnProperty("regnskap.aarsregnskap-copy.enabled")
@Controller
public class AarsregnskapController {

    private final AarsregnskapCopyService aarsregnskapCopyService;


    public AarsregnskapController(AarsregnskapCopyService aarsregnskapCopyService) {
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
}
