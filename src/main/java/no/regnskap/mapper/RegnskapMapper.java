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
            mapped.setRevisorberetningIkkeLevert(xml.getHead().isRevisorberetningIkkeLevert());

            mapped.setAvslutningsdato(localDateFromXmlDateString(xml.getHead().getAvslutningsdato()));
            mapped.setMottattDato(localDateFromXmlDateString(xml.getHead().getMottattDato()));
            mapped.setStartdato(localDateFromXmlDateString(xml.getHead().getStartdato()));

            mapped.setFelter(RegnskapsFeltMapper.mapFieldsFromXmlData(mapped.getFelter(), xml.getPosts()));

            toPersist.put(key, mapped);
        }

        return new ArrayList<>(toPersist.values());
    }

    public static Regnskap persistanceToGenerated(no.regnskap.model.persistance.Regnskap persistanceDTO) {
        return new Regnskap()
            .id(persistanceDTO.getId())
            .avviklingsregnskap(persistanceDTO.isAvviklingsregnskap())
            .valuta(persistanceDTO.getValutakode())
            .oppstillingsplan(Regnskap.OppstillingsplanEnum.fromValue(persistanceDTO.getAarsregnskapstype().toLowerCase()))
            .revisjon(
                new Revisjon()
                    .ikkeRevidertAarsregnskap(persistanceDTO.isRevisorberetningIkkeLevert()))
            .regnskapsperiode(
                new Tidsperiode()
                .fraDato(persistanceDTO.getStartdato())
                .tilDato(persistanceDTO.getAvslutningsdato()))
            .regnkapsprinsipper(
                new Regnskapsprinsipper()
                    .smaaForetak(persistanceDTO.isReglerSmaa()))
            .virksomhet(
                new Virksomhet()
                    .organisasjonsnummer(persistanceDTO.getOrgnr())
                    .organisasjonsform(persistanceDTO.getOrgform())
                    .morselskap(persistanceDTO.isMorselskap())
                    .levertAarsregnskap(true)
                    .navn("HentFraEnhetsregisteret")
            )
            .egenkapitalGjeld(persistanceDTO.getFelter().getEgenkapitalGjeld())
            .eiendeler(persistanceDTO.getFelter().getEiendeler())
            .resultatregnskapResultat(persistanceDTO.getFelter().getResultatregnskapResultat());
    }

    private static LocalDate localDateFromXmlDateString(String dateString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(dateString, dateTimeFormatter);
    }

}
