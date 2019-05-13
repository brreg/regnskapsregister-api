package no.regnskap.controller;

import io.swagger.annotations.ApiParam;
import no.regnskap.generated.model.Regnskap;
import no.regnskap.repository.RegnskapManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class RegnskapsApiControllerImpl implements no.regnskap.generated.api.RegnskapsApi {
    private static Logger LOGGER = LoggerFactory.getLogger(RegnskapsApiControllerImpl.class);

    @Autowired
    private RegnskapManager regnskapManager;

    @Override
    public ResponseEntity<Regnskap> getRegnskapById(HttpServletRequest httpServletRequest, @ApiParam(value = "id",required=true) @PathVariable("id") String id) {
        Regnskap regnskap;

        try {
            regnskap = regnskapManager.getRegnskapById(id);
        } catch (Exception e) {
            LOGGER.error("GET_GETINVOICE failed:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (regnskap==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(regnskap, HttpStatus.OK);
        }
    }
}