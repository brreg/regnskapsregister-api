package no.regnskap.repository;

import no.regnskap.model.Checksum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecksumRepository extends MongoRepository<Checksum, String> {
    Checksum findOneByChecksum(String checksum);
}
