package no.brreg.regnskap.repository;

import no.brreg.regnskap.model.AarsregnskapFileMeta;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static no.brreg.regnskap.config.JdbcConfig.AARDB_JDBC_TEMPLATE;

@Primary
@Repository
public class AarsregnskapH2Repository implements AarsregnskapRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AarsregnskapH2Repository(@Qualifier(AARDB_JDBC_TEMPLATE) NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<AarsregnskapFileMeta> getAarsregnskapMeta(String orgnr) {
        return jdbcTemplate.query("SELECT regnaar, filePath AS path FROM aarsregnskap WHERE orgnr = :orgnr", Map.of("orgnr", orgnr), new AarsregnskapFileMetaRowMapper());
    }

    @Override
    public List<AarsregnskapFileMeta> getBaerekraftMeta(String orgnr) {
        return jdbcTemplate.query("SELECT regnaar, filePath AS path FROM baerekraft WHERE orgnr = :orgnr", Map.of("orgnr", orgnr), new AarsregnskapFileMetaRowMapper());
    }
}
