package no.regnskap.mapper;

import no.regnskap.generated.model.Virksomhet;
import no.regnskap.model.persistance.RegnskapFelt;
import no.regnskap.model.persistance.Regnskap;
import no.regnskap.model.xml.RegnskapXml;
import no.regnskap.model.xml.RegnskapXmlInfo;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RegnskapMapper {



    public static List<Regnskap> mapFromXmlForPersistance(RegnskapXml[] regnskapXml) {
        Map<String, Regnskap> toPersist = new HashMap<>();
        for (RegnskapXml xml : regnskapXml) {
            String key = xml.getHead().getOrgnr() + xml.getHead().getRegnaar();
            Regnskap mapped = toPersist.getOrDefault(key, new Regnskap());

            mapped.setOrgnr(xml.getHead().getOrgnr());
            mapped.setAarsregnskapstype(xml.getHead().getAarsregnskapstype());
            mapped.setAvslutningsdato(xml.getHead().getAvslutningsdato());
            mapped.setAvviklingsregnskap(xml.getHead().isAvviklingsregnskap());
            mapped.setBistandRegnskapsforer(xml.getHead().isBistandRegnskapsforer());
            mapped.setFeilvaloer(xml.getHead().isFeilvaloer());
            mapped.setFleksiblePoster(xml.getHead().isFleksiblePoster());
            mapped.setFravalgRevisjon(xml.getHead().isFravalgRevisjon());
            mapped.setJournalnr(xml.getHead().getJournalnr());
            mapped.setLandForLand(xml.getHead().isLandForLand());
            mapped.setMorselskap(xml.getHead().isMorselskap());
            mapped.setMottakstype(xml.getHead().getMottakstype());
            mapped.setMottattDato(xml.getHead().getMottattDato());
            mapped.setOppstillingsplanVersjonsnr(xml.getHead().getOppstillingsplanVersjonsnr());
            mapped.setOrgform(xml.getHead().getOrgform());
            mapped.setReglerSmaa(xml.getHead().isReglerSmaa());
            mapped.setRegnaar(xml.getHead().getRegnaar());
            mapped.setRegnskapstype(xml.getHead().getRegnskapstype());
            mapped.setStartdato(xml.getHead().getStartdato());
            mapped.setUtarbeidetRegnskapsforer(xml.getHead().isUtarbeidetRegnskapsforer());
            mapped.setValutakode(xml.getHead().getValutakode());

            Set<RegnskapFelt> fields = new HashSet<>();
            if(mapped.getFelter() != null) {
                fields.addAll(mapped.getFelter());
            }

            for (RegnskapXmlInfo post : xml.getPosts()) {
                RegnskapFelt field = new RegnskapFelt();

                field.setFeltkode(post.getFeltkode());
                field.setSum(post.getSum());

                fields.add(field);
            }

            mapped.setFelter(new ArrayList<>(fields));

            toPersist.put(key, mapped);
        }

        return new ArrayList<>(toPersist.values());
    }

    public static no.regnskap.generated.model.Regnskap persistanceToGenerated(Regnskap persistanceDTO) {
        no.regnskap.generated.model.Regnskap regnskap = new no.regnskap.generated.model.Regnskap();
        regnskap.setId(persistanceDTO.getId());
        regnskap.setAvviklingsregnskap(persistanceDTO.isAvviklingsregnskap());

        Virksomhet virksomhet = new Virksomhet();
        virksomhet.setOrganisasjonsnummer(persistanceDTO.getOrgnr());
        virksomhet.setOrganisasjonsform(persistanceDTO.getOrgform());
        virksomhet.setMorselskap(persistanceDTO.isMorselskap());
        regnskap.setVirksomhet(virksomhet);

        return regnskap;
    }

}
