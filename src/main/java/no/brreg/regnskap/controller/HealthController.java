package no.brreg.regnskap.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.sql.DataSource;
import java.sql.SQLException;

import static no.brreg.regnskap.config.PostgresJdbcConfig.RREGAPIDB_DATASOURCE;
import static no.brreg.regnskap.config.SybaseJdbcConfig.AARDB_DATASOURCE;


@Controller
public class HealthController {

    private final DataSource rregapidb;
    private final DataSource aardb;

    public HealthController(@Qualifier(RREGAPIDB_DATASOURCE) DataSource rregapidb, @Qualifier(AARDB_DATASOURCE) DataSource aardb) {
        this.rregapidb = rregapidb;
        this.aardb = aardb;
    }

    @GetMapping(value="/ping", produces={"text/plain"})
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping(value="/ready")
    public ResponseEntity<Void> getReady() {
        try {
            this.rregapidb.getConnection();
            this.aardb.getConnection();
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        return ResponseEntity.ok().build();
    }

}
