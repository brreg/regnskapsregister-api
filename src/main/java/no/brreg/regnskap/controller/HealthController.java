package no.brreg.regnskap.controller;

import no.brreg.regnskap.repository.RestcallLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Controller
@RestControllerAdvice
public class HealthController {

    @Autowired
    private RestcallLogRepository restcallLogRepository;


    @GetMapping(value="/ping", produces={"text/plain"})
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping(value="/ready")
    public ResponseEntity<Void> getReady() {
        if (restcallLogRepository.isDatabaseReady()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

}
