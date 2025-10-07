package no.brreg.regnskap.model.dbo;

import no.brreg.regnskap.generated.model.Regnskapstype;
import no.brreg.regnskap.model.RegnskapFields;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegnskapTest {

    @Test
    public void regnskapValuesStoredCorrectly() {
        Regnskap regnskap = new Regnskap();
        RegnskapFields fields = new RegnskapFields();

        regnskap.setId(1001);
        regnskap.setOrgnr("123456789");
        regnskap.setRegnskapstype(Regnskapstype.SELSKAP);
        regnskap.setRegnaar(2017);
        regnskap.setOppstillingsplanVersjonsnr("a1");
        regnskap.setValutakode("NOK");
        regnskap.setStartdato(LocalDate.of(2017,1,1));
        regnskap.setAvslutningsdato(LocalDate.of(2017,12,31));
        regnskap.setMottakstype("mottakstype");
        regnskap.setAvviklingsregnskap(false);
        regnskap.setFeilvaloer(false);
        regnskap.setJournalnr("12345");
        regnskap.setMottattDato(LocalDate.of(2018,2,19));
        regnskap.setOrgform("AS");
        regnskap.setMorselskap(false);
        regnskap.setReglerSmaa(true);
        regnskap.setFleksiblePoster(false);
        regnskap.setFravalgRevisjon(true);
        regnskap.setUtarbeidetRegnskapsforer(true);
        regnskap.setBistandRegnskapsforer(true);
        regnskap.setAarsregnskapstype("type1");
        regnskap.setLandForLand(true);
        regnskap.setRevisorberetningIkkeLevert(false);
        regnskap.setIfrsSelskap(false);
        regnskap.setForenkletIfrsSelskap(true);
        regnskap.setIfrsKonsern(false);
        regnskap.setForenkletIfrsKonsern(true);
        regnskap.setFields(fields);

        assertEquals(1001, regnskap.getId());
        assertEquals("123456789", regnskap.getOrgnr());
        assertEquals(Regnskapstype.SELSKAP, regnskap.getRegnskapstype());
        assertEquals(2017, regnskap.getRegnaar());
        assertEquals("a1", regnskap.getOppstillingsplanVersjonsnr());
        assertEquals("NOK", regnskap.getValutakode());
        assertEquals(LocalDate.of(2017,1,1), regnskap.getStartdato());
        assertEquals(LocalDate.of(2017,12,31), regnskap.getAvslutningsdato());
        assertEquals("mottakstype", regnskap.getMottakstype());
        assertEquals(false, regnskap.getAvviklingsregnskap());
        assertEquals(false, regnskap.getFeilvaloer());
        assertEquals("12345", regnskap.getJournalnr());
        assertEquals(LocalDate.of(2018,2,19), regnskap.getMottattDato());
        assertEquals("AS", regnskap.getOrgform());
        assertEquals(false, regnskap.getMorselskap());
        assertEquals(true, regnskap.getReglerSmaa());
        assertEquals(false, regnskap.getFleksiblePoster());
        assertEquals(true, regnskap.getFravalgRevisjon());
        assertEquals(true, regnskap.getUtarbeidetRegnskapsforer());
        assertEquals(true, regnskap.getBistandRegnskapsforer());
        assertEquals("type1", regnskap.getAarsregnskapstype());
        assertEquals(true, regnskap.getLandForLand());
        assertEquals(false, regnskap.getRevisorberetningIkkeLevert());
        assertEquals(false, regnskap.getIfrsSelskap());
        assertEquals(true, regnskap.getForenkletIfrsSelskap());
        assertEquals(false, regnskap.getIfrsKonsern());
        assertEquals(true, regnskap.getForenkletIfrsKonsern());
        assertEquals(fields, regnskap.getFields());
    }


    @Test
    public void regnskapstypeFromStringSelskap() {
        assertEquals(Regnskapstype.SELSKAP, Regnskap.regnskapstypeFromString("S"));
    }

    @Test
    public void regnskapstypeFromStringKonsern() {
        assertEquals(Regnskapstype.KONSERN, Regnskap.regnskapstypeFromString("K"));
    }

    @Test
    public void essentialFieldsIsIncluded() {
        Regnskap regnskap = new Regnskap();
        regnskap.setOrgnr("123456789");
        regnskap.setAvslutningsdato(LocalDate.of(2020,2,1));
        regnskap.setStartdato(LocalDate.of(2019,1,1));
        regnskap.setJournalnr("123");

        assertEquals(true, regnskap.essentialFieldsIncluded());
    }

    @Test
    public void essentialFieldsIsNotIncluded() {
        Regnskap regnskap = new Regnskap();
        regnskap.setOrgnr("123456789");

        assertEquals(false, regnskap.essentialFieldsIncluded());
    }

}
