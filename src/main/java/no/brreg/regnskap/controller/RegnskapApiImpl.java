package no.brreg.regnskap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import no.brreg.regnskap.generated.model.Regnskap;
import no.brreg.regnskap.generated.model.Regnskapstype;
import no.brreg.regnskap.jena.ExternalUrls;
import no.brreg.regnskap.jena.JenaUtils;
import no.brreg.regnskap.mapper.RegnskapFieldsMapper.RegnskapFieldIncludeMode;
import no.brreg.regnskap.model.Partner;
import no.brreg.regnskap.repository.ConnectionManager;
import no.brreg.regnskap.service.RegnskapService;
import no.brreg.regnskap.service.RestcallLogService;
import no.brreg.regnskap.spring.properties.JenaExternalUrlProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import static no.brreg.regnskap.jena.JenaUtils.*;


@Controller
@RestControllerAdvice
public class RegnskapApiImpl implements no.brreg.regnskap.generated.api.RegnskapsregisteretApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegnskapApiImpl.class);

    @Autowired
    private JenaExternalUrlProperties jenaExternalUrlProperties;

    @Autowired
    private RegnskapService regnskapService;

    @Autowired
    private RestcallLogService restcallLogService;

    @Autowired
    private ConnectionManager connectionManager;


    @Override
    public ResponseEntity<List<String>> getLog(HttpServletRequest httpServletRequest) {
        try {
            restcallLogService.logCall(httpServletRequest, "getLog");

            List<String> logList = regnskapService.getLog();

            if (logList == null || logList.size() == 0) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(logList, HttpStatus.OK);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Operation(security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<Object> getRegnskap(HttpServletRequest httpServletRequest, String orgNummer, Integer år, Regnskapstype regnskapstype) {
        try {
            restcallLogService.logCall(httpServletRequest, "getRegnskap", orgNummer);

            RegnskapFieldIncludeMode regnskapFieldIncludeMode = RegnskapFieldIncludeMode.DEFAULT;
            try {
                Partner partner = Partner.fromRequest(connectionManager, httpServletRequest);
                if (partner != null && partner.isAuthorized()) {
                    regnskapFieldIncludeMode = RegnskapFieldIncludeMode.PARTNER;
                }
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            List<Regnskap> regnskapList = regnskapService.getByOrgnr(orgNummer, null, år, regnskapstype, regnskapFieldIncludeMode);

            if (regnskapList == null || regnskapList.size() == 0) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            MimeType negotiatedMimeType = negotiateMimeType(httpServletRequest.getHeader("Accept"));
            if (!JenaUtils.jenaCanSerialize(negotiatedMimeType)) {
                return new ResponseEntity<>(regnskapList, HttpStatus.OK);
            }

            String self = jenaExternalUrlProperties.getRegnskapsregisteretUrl();
            String organizationCatalogue = jenaExternalUrlProperties.getOrganizationCatalogueUrl();
            ExternalUrls urls = new ExternalUrls(self, organizationCatalogue);
            String body = modelToString(createJenaResponse(regnskapList, urls), mimeTypeToJenaFormat(negotiatedMimeType));
            return (ResponseEntity) ResponseEntity.ok().contentType(MediaType.asMediaType(negotiatedMimeType)).body(body);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Operation(security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<Regnskap> getRegnskapById(HttpServletRequest httpServletRequest, String orgNummer, Integer id) {
        try {
            restcallLogService.logCall(httpServletRequest, "getRegnskapById");

            RegnskapFieldIncludeMode regnskapFieldIncludeMode = RegnskapFieldIncludeMode.DEFAULT;
            try {
                Partner partner = Partner.fromRequest(connectionManager, httpServletRequest);
                if (partner != null && partner.isAuthorized()) {
                    regnskapFieldIncludeMode = RegnskapFieldIncludeMode.PARTNER;
                }
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            List<Regnskap> regnskaper = regnskapService.getByOrgnr(orgNummer, id, null, null, regnskapFieldIncludeMode);
            Regnskap regnskap = (regnskaper == null || regnskaper.isEmpty()) ? null : regnskaper.get(0);

            if (regnskap == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            MimeType negotiatedMimeType = negotiateMimeType(httpServletRequest.getHeader("Accept"));
            if (!JenaUtils.jenaCanSerialize(negotiatedMimeType)) {
                return new ResponseEntity<>(regnskap, HttpStatus.OK);
            }

            String self = jenaExternalUrlProperties.getRegnskapsregisteretUrl();
            String organizationCatalogue = jenaExternalUrlProperties.getOrganizationCatalogueUrl();
            ExternalUrls urls = new ExternalUrls(self, organizationCatalogue);
            String body = modelToString(createJenaResponse(regnskap, urls), mimeTypeToJenaFormat(negotiatedMimeType));
            return (ResponseEntity) ResponseEntity.ok().contentType(MediaType.asMediaType(negotiatedMimeType)).body(body);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleBadRequest(HttpMessageNotReadableException e) {
        LOGGER.info("Returning BAD Request: ", e);
        throw e;
    }
}
