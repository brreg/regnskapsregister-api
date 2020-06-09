package no.regnskap.repository;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import no.regnskap.model.RegnskapXml;
import no.regnskap.model.RegnskapXmlWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;


@Component
public class RegnskapLogRepository {
    private static Logger LOGGER = LoggerFactory.getLogger(RegnskapLogRepository.class);

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    private RegnskapRepository regnskapRepository;


    public boolean hasLogged(final String filename) throws SQLException {
        boolean hasLogged = false;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                final String sql = "SELECT COUNT(filename) FROM rreg.regnskaplog WHERE filename=?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, filename);
                    ResultSet rs = stmt.executeQuery();
                    hasLogged = rs.next() && rs.getInt(1)>0;
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
        }

        return hasLogged;
    }

    public void persistRegnskapFile(final String filename, final InputStream regnskapStream) throws IOException, SQLException {
        XmlMapper xmlMapper = new XmlMapper();
        RegnskapXmlWrap regnskapXmlWrap = xmlMapper.readValue(regnskapStream, RegnskapXmlWrap.class);

        try (Connection connection = connectionManager.getConnection()) {
            try {
                long persisCount = 0;

                //Persist all regnskap
                for (RegnskapXml regnskapXml : regnskapXmlWrap.getList()) {
                    if (regnskapXml.essentialFieldsIncluded()) {
                        regnskapRepository.persistRegnskap(connection, regnskapXml);
                        persisCount++;
                    } else {
                        LOGGER.info("Skipping " + regnskapXml.getReference() + " from file " + filename + ". Missing essential fields.");
                    }
                }

                //Persist filename log entry
                final String sql = "INSERT INTO rreg.regnskaplog (filename,logtime) " +
                                   "VALUES (?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, filename);
                    stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.executeUpdate();
                }

                LOGGER.info("Persisted " + persisCount + " regnskap from " + filename);
                connection.commit();
            } catch (Exception e) {
                try {
                    connection.rollback();
                    throw e;
                } catch (SQLException e2) {
                    throw e2;
                }
            }
        }
    }

}
