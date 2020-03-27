package no.regnskap.repository;

import no.regnskap.model.RegnskapLog;
import no.regnskap.model.RestcallLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestcallLogRepository extends MongoRepository<RestcallLog, String> {
    //RegnskapLog findOneByFilename(String filename);
}
