package no.regnskap.controller;

import no.regnskap.repository.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;


@Controller
@RestControllerAdvice
public class TestApiImpl implements no.regnskap.generated.api.TestApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestApiImpl.class);

    @Autowired
    private ConnectionManager connectionManager;


    @Override
    public ResponseEntity<String> getMostRecent(HttpServletRequest httpServletRequest) {
        String orgnr = null;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                String sql = "SELECT orgnr FROM rreg.regnskap ORDER BY _id DESC LIMIT 1";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        orgnr = rs.getString("orgnr");
                    }
                }
                connection.commit();
            } catch (Exception e) {
                try {
                    connection.rollback();
                    throw e;
                } catch (SQLException e2) {
                    throw e2;
                }
            }
        } catch (SQLException throwables) {
            LOGGER.error("getConnection failed: " + throwables.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (orgnr == null || orgnr.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(orgnr, HttpStatus.OK);
        }
    }

}
