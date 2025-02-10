package no.brreg.regnskap.repository;

import no.brreg.regnskap.controller.exception.InternalServerError;
import no.brreg.regnskap.model.AarsregnskapFileMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

import static no.brreg.regnskap.config.CacheConfig.CACHE_AAR_COPY_FILEMETA;
import static no.brreg.regnskap.config.CacheConfig.CACHE_BAEREKRAFT_FILEMETA;
import static no.brreg.regnskap.config.JdbcConfig.AARDB_JDBC_TEMPLATE;

@Repository
@ConditionalOnProperty("regnskap.aarsregnskap-copy.enabled")
public class AarsregnskapSybaseRepository implements AarsregnskapRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(AarsregnskapSybaseRepository.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AarsregnskapSybaseRepository(@Qualifier(AARDB_JDBC_TEMPLATE) NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Cacheable(value=CACHE_AAR_COPY_FILEMETA, key = "#orgnr")
    public List<AarsregnskapFileMeta> getAarsregnskapMeta(String orgnr) {
        try {
            return jdbcTemplate.query("exec aardb..finn_sti_image_for_alle_aar :orgnr", Map.of("orgnr", Integer.parseInt(orgnr)), new AarsregnskapFileMetaRowMapper());
        } catch (DataAccessException dae) {
            LOGGER.error("DataAccessException caught. {}. Throwing InternalServerError", dae.getMessage());
            throw new InternalServerError(dae);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Cacheable(value=CACHE_BAEREKRAFT_FILEMETA, key = "#orgnr")
    public List<AarsregnskapFileMeta> getBaerekraftMeta(String orgnr) {
        try {
            return jdbcTemplate.query("exec aardb..finn_berekraft_filepath_for_alle_aar :orgnr", Map.of("orgnr", Integer.parseInt(orgnr)), new AarsregnskapFileMetaRowMapper());
        } catch (DataAccessException dae) {
            LOGGER.error("DataAccessException caught. {}. Throwing InternalServerError", dae.getMessage());
            throw new InternalServerError(dae);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
