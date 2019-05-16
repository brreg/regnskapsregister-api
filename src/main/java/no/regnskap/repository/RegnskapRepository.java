package no.regnskap.repository;

import no.regnskap.service.xml.RegnskapXml;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegnskapRepository extends MongoRepository<RegnskapXml, String> {
    List<RegnskapXml> findByRegnskapInformasjonOrgnr(String orgnr);
}
