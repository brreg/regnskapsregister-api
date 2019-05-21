package no.regnskap.repository;

import no.regnskap.model.persistance.Regnskap;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegnskapRepository extends MongoRepository<Regnskap, String> {
    public List<Regnskap> findByOrgnr(String orgnr);
}
