package no.regnskap.mapper;

import no.regnskap.generated.model.*;
import no.regnskap.model.persistance.RegnskapDB;
import no.regnskap.model.RegnskapXml;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RegnskapMapper {
    private static final String XML_TRUE_STRING = "J";

    public static List<RegnskapDB> mapFromXmlForPersistance(List<RegnskapXml> regnskapXml) {
        Map<String, RegnskapDB> toPersist = new HashMap<>();
        for (RegnskapXml xml : regnskapXml) {
            String key = xml.getHead().getOrgnr() + xml.getHead().getRegnaar();
            RegnskapDB mapped = toPersist.getOrDefault(key, new RegnskapDB());

            mapped.setOrgnr(xml.getHead().getOrgnr());
            mapped.setAarsregnskapstype(xml.getHead().getAarsregnskapstype());
            mapped.setJournalnr(xml.getHead().getJournalnr());
            mapped.setMottakstype(xml.getHead().getMottakstype());
            mapped.setOppstillingsplanVersjonsnr(xml.getHead().getOppstillingsplanVersjonsnr());
            mapped.setOrgform(xml.getHead().getOrgform());
            mapped.setRegnaar(xml.getHead().getRegnaar());
            mapped.setRegnskapstype(xml.getHead().getRegnskapstype());
            mapped.setValutakode(xml.getHead().getValutakode());

            mapped.setAvviklingsregnskap(booleanFromXmlData(xml.getHead().getAvviklingsregnskap()));
            mapped.setBistandRegnskapsforer(booleanFromXmlData(xml.getHead().getBistandRegnskapsforer()));
            mapped.setFeilvaloer(booleanFromXmlData(xml.getHead().getFeilvaloer()));
            mapped.setFleksiblePoster(booleanFromXmlData(xml.getHead().getFleksiblePoster()));
            mapped.setFravalgRevisjon(booleanFromXmlData(xml.getHead().getFravalgRevisjon()));
            mapped.setLandForLand(booleanFromXmlData(xml.getHead().getLandForLand()));
            mapped.setMorselskap(booleanFromXmlData(xml.getHead().getMorselskap()));
            mapped.setReglerSmaa(booleanFromXmlData(xml.getHead().getReglerSmaa()));
            mapped.setUtarbeidetRegnskapsforer(booleanFromXmlData(xml.getHead().getUtarbeidetRegnskapsforer()));
            mapped.setRevisorberetningIkkeLevert(booleanFromXmlData(xml.getHead().getRevisorberetningIkkeLevert()));

            mapped.setAvslutningsdato(localDateFromXmlDateString(xml.getHead().getAvslutningsdato()));
            mapped.setMottattDato(localDateFromXmlDateString(xml.getHead().getMottattDato()));
            mapped.setStartdato(localDateFromXmlDateString(xml.getHead().getStartdato()));

            mapped.setFields(RegnskapFieldsMapper.mapFieldsFromXmlData(mapped.getFields(), xml.getPosts()));

            toPersist.put(key, mapped);
        }

        return new ArrayList<>(toPersist.values());
    }

    private static boolean booleanFromXmlData(String trueOrFalse) {
        return XML_TRUE_STRING.equals(trueOrFalse);
    }

    public static Regnskap persistanceToGenerated(RegnskapDB persistanceDTO) {
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
                    .smaaForetak(persistanceDTO.isReglerSmaa())
                    .regskapsregler(null)) //TODO Change to correct value
            .virksomhet(
                new Virksomhet()
                    .organisasjonsnummer(persistanceDTO.getOrgnr())
                    .organisasjonsform(persistanceDTO.getOrgform())
                    .morselskap(persistanceDTO.isMorselskap())
                    .levertAarsregnskap(true) // TODO Change to correct value
                    .navn(null)) // TODO Change to correct value
            .egenkapitalGjeld(persistanceDTO.getFields().getEgenkapitalGjeld())
            .eiendeler(persistanceDTO.getFields().getEiendeler())
            .resultatregnskapResultat(persistanceDTO.getFields().getResultatregnskapResultat());
    }

    private static LocalDate localDateFromXmlDateString(String dateString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(dateString, dateTimeFormatter);
    }

}
