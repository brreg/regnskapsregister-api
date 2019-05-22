package no.regnskap.mapper;

import no.regnskap.generated.model.*;
import no.regnskap.model.xml.RegnskapXml;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RegnskapMapper {

    public static List<no.regnskap.model.persistance.Regnskap> mapFromXmlForPersistance(RegnskapXml[] regnskapXml) {
        Map<String, no.regnskap.model.persistance.Regnskap> toPersist = new HashMap<>();
        for (RegnskapXml xml : regnskapXml) {
            String key = xml.getHead().getOrgnr() + xml.getHead().getRegnaar();
            no.regnskap.model.persistance.Regnskap mapped = toPersist.getOrDefault(key, new no.regnskap.model.persistance.Regnskap());

            mapped.setOrgnr(xml.getHead().getOrgnr());
            mapped.setAarsregnskapstype(xml.getHead().getAarsregnskapstype());
            mapped.setJournalnr(xml.getHead().getJournalnr());
            mapped.setMottakstype(xml.getHead().getMottakstype());
            mapped.setOppstillingsplanVersjonsnr(xml.getHead().getOppstillingsplanVersjonsnr());
            mapped.setOrgform(xml.getHead().getOrgform());
            mapped.setRegnaar(xml.getHead().getRegnaar());
            mapped.setRegnskapstype(xml.getHead().getRegnskapstype());
            mapped.setValutakode(xml.getHead().getValutakode());

            mapped.setAvviklingsregnskap(xml.getHead().isAvviklingsregnskap());
            mapped.setBistandRegnskapsforer(xml.getHead().isBistandRegnskapsforer());
            mapped.setFeilvaloer(xml.getHead().isFeilvaloer());
            mapped.setFleksiblePoster(xml.getHead().isFleksiblePoster());
            mapped.setFravalgRevisjon(xml.getHead().isFravalgRevisjon());
            mapped.setLandForLand(xml.getHead().isLandForLand());
            mapped.setMorselskap(xml.getHead().isMorselskap());
            mapped.setReglerSmaa(xml.getHead().isReglerSmaa());
            mapped.setUtarbeidetRegnskapsforer(xml.getHead().isUtarbeidetRegnskapsforer());

            mapped.setAvslutningsdato(localDateFromXmlDateString(xml.getHead().getAvslutningsdato()));
            mapped.setMottattDato(localDateFromXmlDateString(xml.getHead().getMottattDato()));
            mapped.setStartdato(localDateFromXmlDateString(xml.getHead().getStartdato()));

            mapped.setFelter(RegnskapsFeltMapper.mapFieldsFromXmlData(mapped.getFelter(), xml.getPosts()));

            toPersist.put(key, mapped);
        }

        return new ArrayList<>(toPersist.values());
    }

    public static Regnskap persistanceToGenerated(no.regnskap.model.persistance.Regnskap persistanceDTO) {
        Regnskap regnskap = new Regnskap();
        regnskap.setId(persistanceDTO.getId());
        regnskap.setAvviklingsregnskap(persistanceDTO.isAvviklingsregnskap());
        regnskap.setValuta(persistanceDTO.getValutakode());

        Virksomhet virksomhet = new Virksomhet();
        virksomhet.setOrganisasjonsnummer(persistanceDTO.getOrgnr());
        virksomhet.setOrganisasjonsform(persistanceDTO.getOrgform());
        virksomhet.setMorselskap(persistanceDTO.isMorselskap());
        regnskap.setVirksomhet(virksomhet);

        Tidsperiode tidsperiode = new Tidsperiode();
        tidsperiode.setFraDato(persistanceDTO.getStartdato());
        tidsperiode.setTilDato(persistanceDTO.getAvslutningsdato());
        regnskap.setRegnskapsperiode(tidsperiode);

        regnskap.setOppstillingsplan(Regnskap.OppstillingsplanEnum.fromValue(persistanceDTO.getAarsregnskapstype().toLowerCase()));

        Revisjon revisjon = new Revisjon();
        regnskap.setRevisjon(revisjon);

        regnskap.setRegnkapsprinsipper(null);

        regnskap.setResultatregnskapResultat(persistanceDTO.getFelter().getResultatregnskapResultat());
        regnskap.setEgenkapitalGjeld(persistanceDTO.getFelter().getEgenkapitalGjeld());
        regnskap.setEiendeler(persistanceDTO.getFelter().getEiendeler());

        return regnskap;
    }

    private static LocalDate localDateFromXmlDateString(String dateString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(dateString, dateTimeFormatter);
    }

}
