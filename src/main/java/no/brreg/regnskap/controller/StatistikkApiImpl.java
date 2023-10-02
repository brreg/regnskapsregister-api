package no.brreg.regnskap.controller;

import no.brreg.regnskap.service.RestcallLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;


@Controller
@RestControllerAdvice
public class StatistikkApiImpl implements no.brreg.regnskap.generated.api.StatistikkApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatistikkApiImpl.class);

    @Autowired
    private RestcallLogService restcallLogService;


    @Override
    public ResponseEntity<List<String>> getStatisticsByIp(HttpServletRequest httpServletRequest, LocalDate fraDato, LocalDate tilDato) {
        try {
            List<String> logList = restcallLogService.getStatisticsByIp(fraDato, tilDato);

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
    public ResponseEntity<List<String>> getStatisticsByMethod(HttpServletRequest httpServletRequest, LocalDate fraDato, LocalDate tilDato) {
        try {
            List<String> logList = restcallLogService.getStatisticsByMethod(fraDato, tilDato);

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
    public ResponseEntity<List<String>> getStatisticsByOrgnr(HttpServletRequest httpServletRequest, LocalDate fraDato, LocalDate tilDato) {
        try {
            List<String> logList = restcallLogService.getStatisticsByOrgnr(fraDato, tilDato);

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

}
