package no.brreg.regnskap.repository;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import no.brreg.regnskap.model.RegnskapXml;
import no.brreg.regnskap.model.RegnskapXmlWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static no.brreg.regnskap.config.PostgresJdbcConfig.RREGAPIDB_DATASOURCE;


@Repository
@Transactional
public class RegnskapLogRepository {
    private static Logger LOGGER = LoggerFactory.getLogger(RegnskapLogRepository.class);

    private final DataSource dataSource;
    private final RegnskapRepository regnskapRepository;

    public RegnskapLogRepository(@Qualifier(RREGAPIDB_DATASOURCE) DataSource dataSource, RegnskapRepository regnskapRepository) {
        this.dataSource = dataSource;
        this.regnskapRepository = regnskapRepository;
    }


    public boolean hasLogged(final String filename) throws SQLException {
        boolean hasLogged = false;
        try (Connection connection = dataSource.getConnection()) {
            final String sql = "SELECT COUNT(filename) FROM rregapi.regnskaplog WHERE filename=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, filename);
                ResultSet rs = stmt.executeQuery();
                hasLogged = rs.next() && rs.getInt(1)>0;
                LOGGER.info("Has " + (hasLogged?"":"NOT ") + "logged " + filename);
            }
        }

        return hasLogged;
    }

    public void persistRegnskapFile(final String filename, final InputStream regnskapStream) throws IOException, SQLException {
        File tmpFile = null;
        File tmpZipFile = null;
        try (Connection connection = dataSource.getConnection()) {
            { //Just a scope for buffer[] ...
                byte[] buffer = new byte[100 * 1024]; //100KB chunks
                int bytesRead;

                //Dump regnskapStream to temp file
                tmpFile = File.createTempFile("rreg-", ".xml");
                try (OutputStream os = new FileOutputStream(tmpFile)) {
                    while ((bytesRead = regnskapStream.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }

                //Zip file
                tmpZipFile = File.createTempFile("rreg-", ".zip");
                try (FileInputStream fis = new FileInputStream(tmpFile);
                     FileOutputStream fos = new FileOutputStream(tmpZipFile);
                     ZipOutputStream zos = new ZipOutputStream(fos)) {
                    zos.putNextEntry(new ZipEntry(filename));
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }
                }
            }

            try (FileInputStream xmlFis = new FileInputStream(tmpFile);
                 FileInputStream zipFis = new FileInputStream(tmpZipFile)) {
                XmlMapper xmlMapper = new XmlMapper();
                RegnskapXmlWrap regnskapXmlWrap = xmlMapper.readValue(xmlFis, RegnskapXmlWrap.class);

                //Persist filename log entry
                Integer regnskapLogId = null;
                final String sql = "INSERT INTO rregapi.regnskaplog (filename,logtime,zipfile) " +
                        "VALUES (?,?,?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, filename);
                    stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.setBinaryStream(3, zipFis);
                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        regnskapLogId = rs.getInt(1);
                    }
                }

                //Persist all regnskap
                long persisCount = 0;
                for (RegnskapXml regnskapXml : regnskapXmlWrap.getList()) {
                    if (regnskapXml.essentialFieldsIncluded()) {
                        regnskapRepository.persistRegnskap(connection, regnskapXml, regnskapLogId);
                        persisCount++;
                    } else {
                        LOGGER.info("Skipping " + regnskapXml.getReference() + " from file " + filename + ". Missing essential fields.");
                    }
                }

                LOGGER.info("Persisted " + persisCount + " regnskap from " + filename);
            }
        } finally {
            if (tmpFile != null) {
                if(!tmpFile.delete()) {
                    LOGGER.error("Temporary file could not be deleted: ", tmpFile.getName());
                }
            }

            if (tmpZipFile != null) {
                if(!tmpZipFile.delete()) {
                    LOGGER.error("Temporary file could not be deleted: ", tmpZipFile.getName());
                }
            }
        }
    }

}
