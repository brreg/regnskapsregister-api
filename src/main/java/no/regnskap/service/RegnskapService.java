package no.regnskap.service;

import no.regnskap.generated.model.Regnskap;
import no.regnskap.mapper.RegnskapMapper;
import no.regnskap.repository.RegnskapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegnskapService {

    @Autowired
    private RegnskapRepository regnskapRepository;

    @Autowired
    private RegnskapMapper regnskapMapper;

    public Optional<Regnskap> getById(String id) {
        return regnskapRepository
                .findById(id)
                .flatMap(persistedData -> Optional.of(regnskapMapper.persistanceToGenerated(persistedData)));
    }

    public List<Regnskap> getByOrgnr(String orgnr) {
        return regnskapRepository
                .findByOrgnr(orgnr)
                .stream()
                .map(regnskapMapper::persistanceToGenerated)
                .collect(Collectors.toList());
    }
}
