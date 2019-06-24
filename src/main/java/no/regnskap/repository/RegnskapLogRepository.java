package no.regnskap.repository;

import no.regnskap.model.RegnskapLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegnskapLogRepository extends MongoRepository<RegnskapLog, String> {
    RegnskapLog findOneByFilename(String filename);
}
