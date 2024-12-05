package no.brreg.regnskap.repository;

import no.brreg.regnskap.model.AarsregnskapFileMeta;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AarsregnskapFileMetaRowMapper implements RowMapper<AarsregnskapFileMeta> {
    @Override
    public AarsregnskapFileMeta mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new AarsregnskapFileMeta(rs.getString("regnaar"), rs.getString("path").trim());
    }
}
