package no.regnskap.repository;

import no.regnskap.service.xml.RegnskapWrap;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegnskapRepository extends MongoRepository<RegnskapWrap, String> {
    List<RegnskapWrap> findByChecksum(String checksum);
}
