package no.brreg.regnskap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.brreg.regnskap.generated.model.ServerErrorRespons;
import no.brreg.regnskap.service.AarsregnskapCopyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@ConditionalOnProperty("regnskap.aarsregnskap-copy.external-enabled")
@Controller
@RestControllerAdvice
@Tag(name = "Kopi av årsregnskap")
public class AarsregnskapController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AarsregnskapController.class);

    private final AarsregnskapCopyService aarsregnskapCopyService;


    public AarsregnskapController(AarsregnskapCopyService aarsregnskapCopyService) {
        this.aarsregnskapCopyService = aarsregnskapCopyService;
    }

    @Operation(
            operationId = "getAvailableAarsregnskap",
            summary = "Hent ut en liste over år hvor kopi av årsregnskap er tilgjengelig",
            parameters = {
                    @Parameter(name = "orgnr", in = ParameterIn.PATH, description = "Virksomhetens organisasjonsnummer", schema = @Schema(implementation = String.class))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK", content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = String.class)))
                    }),
                    @ApiResponse(responseCode = "500", description = "Feil oppstod", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ServerErrorRespons.class))
                    })
            }
    )

    @GetMapping(path = "/regnskapsregisteret/aarsregnskap/kopi/{orgnr}/aar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAvailableAarsregnskap(@PathVariable String orgnr) {
        var yearList = this.aarsregnskapCopyService.getAvailableAarsregnskapYears(orgnr);

        return ResponseEntity
                .status(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body(yearList);
    }

    @Operation(
            operationId = "getAarsregnskapCopy",
            summary = "Hent ut kopi av årsregnskap som PDF",
            parameters = {
                    @Parameter(name = "orgnr", in = ParameterIn.PATH, description = "Virksomhetens organisasjonsnummer", schema = @Schema(implementation = String.class)),
                    @Parameter(name = "aar", in = ParameterIn.PATH, description = "Regnskapsår", schema = @Schema(implementation = String.class))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = StreamingResponseBody.class))),
                    @ApiResponse(responseCode = "500", description = "Feil oppstod", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ServerErrorRespons.class))
                    })
            }
    )
    @GetMapping(path = "/regnskapsregisteret/aarsregnskap/kopi/{orgnr}/{aar}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
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
