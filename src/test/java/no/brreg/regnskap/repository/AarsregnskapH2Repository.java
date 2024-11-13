package no.brreg.regnskap.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static no.brreg.regnskap.config.JdbcConfig.AARDB_JDBC_TEMPLATE;

@Primary
@Repository
public class AarsregnskapH2Repository implements AarsregnskapRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AarsregnskapH2Repository(@Qualifier(AARDB_JDBC_TEMPLATE) NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<String> getAvailableAarsregnskap(String orgnr) {
        return jdbcTemplate.query("SELECT regnaar, filePath AS path FROM aarsregnskap WHERE orgnr = :orgnr", Map.of("orgnr", orgnr), new AarsregnskapYearResultSetExtractor());
    }

    @Override
    public Optional<String> getAarsregnskapPath(String orgnr, String year) {
        var params = Map.of(
                "orgnr", orgnr,
                "year", year
        );
        return jdbcTemplate.queryForStream("SELECT filePath FROM aarsregnskap WHERE orgnr = :orgnr AND regnaar = :year", params, (rs, rowNum) -> rs.getString("filePath"))
                .findFirst();
    }
}
