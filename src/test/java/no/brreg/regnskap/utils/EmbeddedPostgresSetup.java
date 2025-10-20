package no.brreg.regnskap.utils;

import no.brreg.regnskap.configuration.TestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static no.brreg.regnskap.config.PostgresJdbcConfig.RREGAPIDB_DATASOURCE;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestConfig.class})
@WebAppConfiguration
public abstract class EmbeddedPostgresSetup {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmbeddedPostgresSetup.class);

    @Autowired
    @Qualifier(RREGAPIDB_DATASOURCE)
    private DataSource dataSource;

    @AfterEach
    public void deleteTestDataFromDatabase() {
        try {
            String[] tables = {"partners", "regnskap", "regnskaplog", "felt", "restcallog"};
            Connection connection = dataSource.getConnection();
            for (String t : tables) {
                PreparedStatement stmt = connection.prepareStatement("DELETE FROM rregapi." + t);
                stmt.executeUpdate();
            }
            PreparedStatement idReset = connection.prepareStatement("ALTER SEQUENCE rregapi.regnskap__id_seq RESTART");
            idReset.executeUpdate();
        } catch (SQLException e) {
            LOGGER.info("Could not delete test data");
        }
    }

}
