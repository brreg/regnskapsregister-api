package no.regnskap.controller;

import io.swagger.annotations.ApiParam;
import no.regnskap.generated.model.Regnskap;
import no.regnskap.repository.RegnskapRepository;
import no.regnskap.service.RegnskapService;
import no.regnskap.service.xml.RegnskapWrap;
import no.regnskap.service.xml.RegnskapXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class RegnskapApiControllerImpl implements no.regnskap.generated.api.RegnskapApi {
    private static Logger LOGGER = LoggerFactory.getLogger(RegnskapApiControllerImpl.class);

    @Autowired
    private RegnskapService service;

    @Autowired
    private RegnskapRepository repository;

    @RequestMapping(value="/ping", method=GET, produces={"text/plain"})
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("pong");
    }

    @RequestMapping(value="/ready", method=GET)
    public ResponseEntity getReady() {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value="/regnskap/update", method=GET)
    public ResponseEntity<RegnskapWrap> checkForUpdatedFile(HttpServletRequest httpServletRequest) throws IOException, SQLException {
        return new ResponseEntity<>(service.update(), HttpStatus.OK);
    }
/*
    @RequestMapping(value="/regnskap/find", method=GET)
    public ResponseEntity<List<RegnskapXml>> findOne(HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(repository.findByListHeadOrgnr("980919676"), HttpStatus.OK);
    }
*/
    @Override
    public ResponseEntity<List<Regnskap>> getRegnskap(HttpServletRequest httpServletRequest, @ApiParam(value = "Virksomhetens organisasjonsnummer") @Valid @RequestParam(value = "orgNummer", required = false) String orgNummer) {
        List<Regnskap> regnskap;

        try {
            //regnskap = repository.findByOrgOgRegInfoOrgnr(orgNummer);
        } catch (Exception e) {
            LOGGER.error("getRegnskap failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
/*
        if (regnskap==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(regnskap, HttpStatus.OK);
        }
    }
*/
}