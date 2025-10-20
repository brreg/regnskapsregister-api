package no.brreg.regnskap.controller;

import org.springframework.beans.factory.ObjectProvider;
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
    private final ObjectProvider<DataSource> aardbProvider;

    public HealthController(@Qualifier(RREGAPIDB_DATASOURCE) DataSource rregapidb, @Qualifier(AARDB_DATASOURCE) ObjectProvider<DataSource> aardbProvider) {
        this.rregapidb = rregapidb;
        this.aardbProvider = aardbProvider;
    }

    @GetMapping(value="/ping", produces={"text/plain"})
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping(value="/ready")
    public ResponseEntity<Void> getReady() {
        try {
            this.rregapidb.getConnection().isValid(5);

            var aardb = this.aardbProvider.getIfAvailable();
            if (aardb != null) {
                aardb.getConnection().isValid(5);
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        return ResponseEntity.ok().build();
    }
}
