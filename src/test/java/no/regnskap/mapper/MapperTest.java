package no.regnskap.mapper;

import no.regnskap.XmlTestData;
import no.regnskap.generated.model.Regnskap;
import no.regnskap.model.RegnskapDB;
import no.regnskap.model.RegnskapFieldsDB;
import no.regnskap.model.RegnskapXmlWrap;
import no.regnskap.testcategories.UnitTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static no.regnskap.mapper.RegnskapXmlMapperKt.deserializeXmlString;
import static no.regnskap.mapper.RegnskapMapperKt.mapXmlListForPersistence;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTest.class)
public class MapperTest {

    private static RegnskapXmlWrap xmlWrap;
    private static List<RegnskapDB> listRegnskapDB;
    private static List<Regnskap> listRegnskap;

    @BeforeClass
    public static void mappingFromXmlToResponse() throws IOException {
        xmlWrap = deserializeXmlString(XmlTestData.xmlTestString);
        listRegnskapDB = mapXmlListForPersistence(xmlWrap);

        listRegnskap = listRegnskapDB
            .stream()
            .map(RegnskapMapperKt::mapPersistenceToGenerated)
            .collect(Collectors.toList());
    }

    @Test
    public void allFieldsAreMapped() {
        Assert.assertEquals(44, xmlWrap.getList().get(0).getPosts().size());
        Assert.assertEquals(60, xmlWrap.getList().get(1).getPosts().size());
        Assert.assertEquals(40, xmlWrap.getList().get(2).getPosts().size());
        Assert.assertEquals(45, xmlWrap.getList().get(3).getPosts().size());
    }

    @Test
    public void equalOrgnrAndRegnaarIsMerged() {
        Assert.assertEquals(4, xmlWrap.getList().size());
        Assert.assertEquals(xmlWrap.getList().get(0).getHead().getOrgnr(),xmlWrap.getList().get(1).getHead().getOrgnr() );
        Assert.assertEquals(xmlWrap.getList().get(2).getHead().getOrgnr(),xmlWrap.getList().get(3).getHead().getOrgnr() );
        Assert.assertNotEquals(xmlWrap.getList().get(0).getHead().getOrgnr(),xmlWrap.getList().get(2).getHead().getOrgnr() );
        Assert.assertEquals(xmlWrap.getList().get(0).getHead().getRegnaar(),xmlWrap.getList().get(1).getHead().getRegnaar() );
        Assert.assertEquals(xmlWrap.getList().get(2).getHead().getRegnaar(),xmlWrap.getList().get(3).getHead().getRegnaar() );
        Assert.assertEquals(xmlWrap.getList().get(1).getHead().getRegnaar(),xmlWrap.getList().get(3).getHead().getRegnaar() );
        Assert.assertEquals(2, listRegnskapDB.size());
    }

    @Test
    public void correctSumsInPersistenceFields() {
        RegnskapFieldsDB fieldsOne = listRegnskapDB.get(0).getFields();
        RegnskapFieldsDB fieldsTwo = listRegnskapDB.get(1).getFields();

        Assert.assertEquals(XmlTestData.ALPHA_FIELD_219, fieldsOne.getEiendeler().getSumEiendeler().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_217, fieldsOne.getEiendeler().getAnleggsmidler().getSumAnleggsmidler().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_194, fieldsOne.getEiendeler().getOmloepsmidler().getSumOmloepsmidler().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_251, fieldsOne.getEgenkapitalGjeld().getSumEgenkapitalGjeld().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_250, fieldsOne.getEgenkapitalGjeld().getEgenkapital().getSumEgenkapital().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_3730, fieldsOne.getEgenkapitalGjeld().getEgenkapital().getInnskuttEgenkapital().getSumInnskuttEgenkaptial().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_9702, fieldsOne.getEgenkapitalGjeld().getEgenkapital().getOpptjentEgenkapital().getSumOpptjentEgenkapital().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_1119, fieldsOne.getEgenkapitalGjeld().getGjeldOversikt().getSumGjeld().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_86, fieldsOne.getEgenkapitalGjeld().getGjeldOversikt().getLangsiktigGjeld().getSumLangsiktigGjeld().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_85, fieldsOne.getEgenkapitalGjeld().getGjeldOversikt().getKortsiktigGjeld().getSumKortsiktigGjeld().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_172, fieldsOne.getResultatregnskapResultat().getAarsresultat().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_36633, fieldsOne.getResultatregnskapResultat().getTotalresultat().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_167, fieldsOne.getResultatregnskapResultat().getOrdinaertResultatFoerSkattekostnad().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_146, fieldsOne.getResultatregnskapResultat().getDriftsresultat().getDriftsresultat().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_72, fieldsOne.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().getSumDriftsinntekter().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_17126, fieldsOne.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().getSumDriftskostnad().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_158, fieldsOne.getResultatregnskapResultat().getFinansresultat().getNettoFinans().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_153, fieldsOne.getResultatregnskapResultat().getFinansresultat().getFinansinntekt().getSumFinansinntekter().intValue());
        Assert.assertEquals(XmlTestData.ALPHA_FIELD_17130, fieldsOne.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getSumFinanskostnad().intValue());

        Assert.assertEquals(XmlTestData.BRAVO_FIELD_219, fieldsTwo.getEiendeler().getSumEiendeler().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_217, fieldsTwo.getEiendeler().getAnleggsmidler().getSumAnleggsmidler().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_194, fieldsTwo.getEiendeler().getOmloepsmidler().getSumOmloepsmidler().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_251, fieldsTwo.getEgenkapitalGjeld().getSumEgenkapitalGjeld().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_250, fieldsTwo.getEgenkapitalGjeld().getEgenkapital().getSumEgenkapital().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_3730, fieldsTwo.getEgenkapitalGjeld().getEgenkapital().getInnskuttEgenkapital().getSumInnskuttEgenkaptial().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_9702, fieldsTwo.getEgenkapitalGjeld().getEgenkapital().getOpptjentEgenkapital().getSumOpptjentEgenkapital().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_1119, fieldsTwo.getEgenkapitalGjeld().getGjeldOversikt().getSumGjeld().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_86, fieldsTwo.getEgenkapitalGjeld().getGjeldOversikt().getLangsiktigGjeld().getSumLangsiktigGjeld().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_85, fieldsTwo.getEgenkapitalGjeld().getGjeldOversikt().getKortsiktigGjeld().getSumKortsiktigGjeld().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_172, fieldsTwo.getResultatregnskapResultat().getAarsresultat().intValue());
        Assert.assertNull(fieldsTwo.getResultatregnskapResultat().getTotalresultat());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_167, fieldsTwo.getResultatregnskapResultat().getOrdinaertResultatFoerSkattekostnad().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_146, fieldsTwo.getResultatregnskapResultat().getDriftsresultat().getDriftsresultat().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_72, fieldsTwo.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().getSumDriftsinntekter().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_17126, fieldsTwo.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().getSumDriftskostnad().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_158, fieldsTwo.getResultatregnskapResultat().getFinansresultat().getNettoFinans().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_153, fieldsTwo.getResultatregnskapResultat().getFinansresultat().getFinansinntekt().getSumFinansinntekter().intValue());
        Assert.assertEquals(XmlTestData.BRAVO_FIELD_17130, fieldsTwo.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getSumFinanskostnad().intValue());
    }

    @Test
    public void correctMappingFromPersistence() {
        RegnskapDB dbAlpha = listRegnskapDB.get(0);
        RegnskapDB dbBravo = listRegnskapDB.get(1);

        Regnskap alpha = listRegnskap.get(0);
        Regnskap bravo = listRegnskap.get(1);

        Assert.assertEquals(dbAlpha.getAvviklingsregnskap(), alpha.getAvviklingsregnskap());
        Assert.assertEquals(dbAlpha.getValutakode(), alpha.getValuta());
        Assert.assertEquals(dbAlpha.getAarsregnskapstype().toLowerCase(), alpha.getOppstillingsplan().getValue());
        Assert.assertEquals(dbAlpha.getRevisorberetningIkkeLevert(), alpha.getAvviklingsregnskap());
        Assert.assertEquals(dbAlpha.getStartdato(), alpha.getRegnskapsperiode().getFraDato());
        Assert.assertEquals(dbAlpha.getAvslutningsdato(), alpha.getRegnskapsperiode().getTilDato());
        Assert.assertEquals(dbAlpha.getReglerSmaa(), alpha.getRegnkapsprinsipper().getSmaaForetak());
        //Assert.assertEquals(dbAlpha.getSomething(), alpha.getRegnkapsprinsipper().getRegnskapsregler()); TODO
        Assert.assertEquals(dbAlpha.getOrgnr(), alpha.getVirksomhet().getOrganisasjonsnummer());
        Assert.assertEquals(dbAlpha.getOrgform(), alpha.getVirksomhet().getOrganisasjonsform());
        Assert.assertEquals(dbAlpha.getMorselskap(), alpha.getVirksomhet().getMorselskap());
        //Assert.assertEquals(dbAlpha.getSomething(), alpha.getAvviklingsregnskap()); TODO
        //Assert.assertEquals(dbAlpha.getSomething(), alpha.getAvviklingsregnskap()); TODO

        Assert.assertEquals(dbBravo.getAvviklingsregnskap(), bravo.getAvviklingsregnskap());
        Assert.assertEquals(dbBravo.getValutakode(), bravo.getValuta());
        Assert.assertEquals(dbBravo.getAarsregnskapstype().toLowerCase(), bravo.getOppstillingsplan().getValue());
        Assert.assertEquals(dbBravo.getRevisorberetningIkkeLevert(), bravo.getAvviklingsregnskap());
        Assert.assertEquals(dbBravo.getStartdato(), bravo.getRegnskapsperiode().getFraDato());
        Assert.assertEquals(dbBravo.getAvslutningsdato(), bravo.getRegnskapsperiode().getTilDato());
        Assert.assertEquals(dbBravo.getReglerSmaa(), bravo.getRegnkapsprinsipper().getSmaaForetak());
        //Assert.assertEquals(dbBravo.getSomething(), bravo.getRegnkapsprinsipper().getRegnskapsregler()); TODO
        Assert.assertEquals(dbBravo.getOrgnr(), bravo.getVirksomhet().getOrganisasjonsnummer());
        Assert.assertEquals(dbBravo.getOrgform(), bravo.getVirksomhet().getOrganisasjonsform());
        Assert.assertEquals(dbBravo.getMorselskap(), bravo.getVirksomhet().getMorselskap());
        //Assert.assertEquals(dbBravo.getSomething(), bravo.getAvviklingsregnskap()); TODO
        //Assert.assertEquals(dbBravo.getSomething(), bravo.getAvviklingsregnskap()); TODO
    }
}
