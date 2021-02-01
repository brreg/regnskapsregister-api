package no.regnskap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import no.regnskap.generated.model.Regnskap;
import no.regnskap.generated.model.Regnskapstype;
import no.regnskap.jena.ExternalUrls;
import no.regnskap.jena.JenaUtils;
import no.regnskap.mapper.RegnskapFieldsMapper;
import no.regnskap.model.Partner;
import no.regnskap.repository.ConnectionManager;
import no.regnskap.service.RegnskapService;
import no.regnskap.service.RestcallLogService;
import no.regnskap.spring.config.ProfileConditionalValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
@RestControllerAdvice
public class RegnskapApiImpl implements no.regnskap.generated.api.RegnskapApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegnskapApiImpl.class);

    @Autowired
    private ProfileConditionalValues profileConditionalValues;

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
        } catch (Exception e) {
            LOGGER.error("getLog failed: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Operation(security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<Object> getRegnskapDeprecated(HttpServletRequest httpServletRequest, String orgNummer, Integer år, Regnskapstype regnskapstype) {
        return getRegnskap(httpServletRequest, orgNummer, år, regnskapstype);
    }

    @Override
    @Operation(security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<Object> getRegnskap(HttpServletRequest httpServletRequest, String orgNummer, Integer år, Regnskapstype regnskapstype) {
        try {
            restcallLogService.logCall(httpServletRequest, "getRegnskap", orgNummer);

            RegnskapFieldsMapper.RegnskapFieldIncludeMode regnskapFieldIncludeMode = RegnskapFieldsMapper.RegnskapFieldIncludeMode.DEFAULT;
            try {
                Partner partner = Partner.fromRequest(connectionManager, httpServletRequest);
                if (partner!=null && partner.isAuthorized()) {
                    regnskapFieldIncludeMode = RegnskapFieldsMapper.RegnskapFieldIncludeMode.PARTNER;
                }
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            List<Regnskap> regnskapList = regnskapService.getByOrgnr(orgNummer, null, år, regnskapstype, regnskapFieldIncludeMode);

            if (regnskapList == null || regnskapList.size() == 0) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                MimeType negotiatedMimeType = JenaUtils.negotiateMimeType(httpServletRequest.getHeader("Accept"));
                if (JenaUtils.jenaCanSerialize(negotiatedMimeType)) {
                    ExternalUrls urls = new ExternalUrls(profileConditionalValues.regnskapsregisteretUrl(),
                                                         profileConditionalValues.organizationCatalogueUrl());
                    String body = JenaUtils.modelToString(JenaUtils.createJenaResponse(regnskapList, urls), JenaUtils.mimeTypeToJenaFormat(negotiatedMimeType));
                    return ResponseEntity.ok().contentType(MediaType.asMediaType(negotiatedMimeType)).body(body);
                } else {
                    return new ResponseEntity<>(regnskapList, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            LOGGER.error("getRegnskap failed: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Operation(security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<Object> getRegnskapById(HttpServletRequest httpServletRequest, String orgNummer, Integer id) {
        try {
            restcallLogService.logCall(httpServletRequest, "getRegnskapById");

            RegnskapFieldsMapper.RegnskapFieldIncludeMode regnskapFieldIncludeMode = RegnskapFieldsMapper.RegnskapFieldIncludeMode.DEFAULT;
            try {
                Partner partner = Partner.fromRequest(connectionManager, httpServletRequest);
                if (partner != null && partner.isAuthorized()) {
                    regnskapFieldIncludeMode = RegnskapFieldsMapper.RegnskapFieldIncludeMode.PARTNER;
                }
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            List<Regnskap> regnskaper = regnskapService.getByOrgnr(orgNummer, id, null, null, regnskapFieldIncludeMode);
            Regnskap regnskap = (regnskaper==null || regnskaper.isEmpty()) ? null : regnskaper.get(0);

            if (regnskap == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                MimeType negotiatedMimeType = JenaUtils.negotiateMimeType(httpServletRequest.getHeader("Accept"));
                if (negotiatedMimeType == null) {
                    return new ResponseEntity<>(regnskap, HttpStatus.OK);
                } else {
                    ExternalUrls urls = new ExternalUrls(profileConditionalValues.regnskapsregisteretUrl(),
                                                         profileConditionalValues.organizationCatalogueUrl());
                    String body = JenaUtils.modelToString(JenaUtils.createJenaResponse(regnskap, urls), JenaUtils.mimeTypeToJenaFormat(negotiatedMimeType));
                    return ResponseEntity.ok().contentType(MediaType.asMediaType(negotiatedMimeType)).body(body);
                }
            }
        } catch (Exception e) {
            LOGGER.error("getRegnskapById failed: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleBadRequest(HttpMessageNotReadableException e) {
        LOGGER.info("Returning BAD Request: ", e);
        throw e;
    }
}
