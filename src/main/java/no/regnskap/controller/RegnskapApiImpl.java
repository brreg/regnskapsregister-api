package no.regnskap.controller;

import no.regnskap.generated.model.Regnskap;
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
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(logList, HttpStatus.OK);
            }
        } catch (Exception e) {
            LOGGER.error("getLog failed: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> getRegnskap(HttpServletRequest httpServletRequest, String orgNummer, Integer år, String regnskapstype) {
        try {
            restcallLogService.logCall(httpServletRequest, "getRegnskap", orgNummer);

            RegnskapFieldsMapper.RegnskapFieldIncludeMode regnskapFieldIncludeMode = RegnskapFieldsMapper.RegnskapFieldIncludeMode.DEFAULT;
            Partner partner = Partner.fromRequest(connectionManager, httpServletRequest);
            if (partner!=null && partner.isAuthorized()) {
                regnskapFieldIncludeMode = RegnskapFieldsMapper.RegnskapFieldIncludeMode.PARTNER;
            }

            List<Regnskap> regnskapList = regnskapService.getByOrgnr(orgNummer, år, regnskapstype, regnskapFieldIncludeMode);

            if (regnskapList == null || regnskapList.size() == 0) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                MimeType negotiatedMimeType = JenaUtils.negotiateMimeType(httpServletRequest.getHeader("Accept"));
                if (negotiatedMimeType == null) {
                    return new ResponseEntity<>(regnskapList, HttpStatus.OK);
                } else {
                    ExternalUrls urls = new ExternalUrls(profileConditionalValues.regnskapsregisteretUrl(),
                                                         profileConditionalValues.organizationCatalogueUrl());
                    String body = JenaUtils.modelToString(JenaUtils.createJenaResponse(regnskapList, urls), JenaUtils.mimeTypeToJenaFormat(negotiatedMimeType));
                    return ResponseEntity.ok().contentType(MediaType.asMediaType(negotiatedMimeType)).body(body);
                }
            }
        } catch (Exception e) {
            LOGGER.error("getRegnskap failed: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> getRegnskapById(HttpServletRequest httpServletRequest, String id) {
        try {
            restcallLogService.logCall(httpServletRequest, "getRegnskapById");

            RegnskapFieldsMapper.RegnskapFieldIncludeMode regnskapFieldIncludeMode = RegnskapFieldsMapper.RegnskapFieldIncludeMode.DEFAULT;
            Partner partner = Partner.fromRequest(connectionManager, httpServletRequest);
            if (partner!=null && partner.isAuthorized()) {
                regnskapFieldIncludeMode = RegnskapFieldsMapper.RegnskapFieldIncludeMode.PARTNER;
            }

            Regnskap regnskap = regnskapService.getById(id, regnskapFieldIncludeMode);

            if (regnskap == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

}
