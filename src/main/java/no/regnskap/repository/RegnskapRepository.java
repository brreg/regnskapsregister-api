package no.regnskap.repository;

import no.regnskap.generated.model.Regnskap;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegnskapRepository extends MongoRepository<Regnskap, String> {
    List<Regnskap> findByVirksomhetOrganisasjonsnummer(String orgnr);
}
