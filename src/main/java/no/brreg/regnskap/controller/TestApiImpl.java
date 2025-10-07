package no.brreg.regnskap.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static no.brreg.regnskap.config.PostgresJdbcConfig.RREGAPIDB_DATASOURCE;


@Controller
public class TestApiImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestApiImpl.class);

    private final DataSource dataSource;

    public TestApiImpl(@Qualifier(RREGAPIDB_DATASOURCE) DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @RequestMapping(
            method = RequestMethod.GET,
            value = "/test/most-recent",
            produces = { "text/plain", "application/json" }
    )
    public ResponseEntity<String> getMostRecent(HttpServletRequest httpServletRequest) {
        String orgnr = null;
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT orgnr FROM rregapi.regnskap ORDER BY _id DESC LIMIT 1";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    orgnr = rs.getString("orgnr");
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (orgnr == null || orgnr.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(orgnr, HttpStatus.OK);
        }
    }
}
