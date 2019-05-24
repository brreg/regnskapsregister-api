package no.regnskap.mapper;

import no.regnskap.TestData;
import no.regnskap.model.RegnskapDB;
import no.regnskap.model.RegnskapFieldsDB;
import no.regnskap.model.RegnskapXmlWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static no.regnskap.mapper.RegnskapMapperKt.mapXmlListForPersistence;
import static no.regnskap.mapper.RegnskapXmlMapperKt.deserializeXmlString;

@RunWith(MockitoJUnitRunner.class)
public class MapperTest {

    @Test
    public void allFieldsAreMapped() throws IOException {
        RegnskapXmlWrap result = deserializeXmlString(TestData.xmlTestString);

        Assert.assertEquals(44, result.getList().get(0).getPosts().size());
        Assert.assertEquals(60, result.getList().get(1).getPosts().size());
        Assert.assertEquals(40, result.getList().get(2).getPosts().size());
        Assert.assertEquals(45, result.getList().get(3).getPosts().size());
    }

    @Test
    public void equalOrgnrAndRegnaarIsMerged() throws IOException {
        RegnskapXmlWrap xmlWrap = deserializeXmlString(TestData.xmlTestString);
        List<RegnskapDB> listRegnskapDB = mapXmlListForPersistence(xmlWrap.getList());

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
    public void correctSumsInPersistenceFields() throws IOException {
        RegnskapXmlWrap xmlWrap = deserializeXmlString(TestData.xmlTestString);
        List<RegnskapDB> listRegnskapDB = mapXmlListForPersistence(xmlWrap.getList());
        RegnskapFieldsDB fieldsOne = listRegnskapDB.get(0).getFields();
        RegnskapFieldsDB fieldsTwo = listRegnskapDB.get(1).getFields();

        int sumFieldCode219One = 4655600;
        int sumFieldCode217One = 3314585;
        int sumFieldCode194One = 1341015;
        int sumFieldCode251One = 4655600;
        int sumFieldCode250One = 750287;
        int sumFieldCode3730One = 50000;
        int sumFieldCode9702One = 700287;
        int sumFieldCode1119One = 3905312;
        int sumFieldCode86One = 1530406;
        int sumFieldCode85One = 2374906;
        int sumFieldCode172One = 427946;
        int sumFieldCode36633One = 427946;
        int sumFieldCode167One = 567732;
        int sumFieldCode146One = 604176;
        int sumFieldCode72One = 10900358;
        int sumFieldCode17126One = 10296182;
        int sumFieldCode158One = -36445;
        int sumFieldCode153One = 3939;
        int sumFieldCode17130One = 40384;

        int sumFieldCode219Two = 1123609;
        int sumFieldCode217Two = 0;
        int sumFieldCode194Two = 1123609;
        int sumFieldCode251Two = 1123609;
        int sumFieldCode250Two = 106310;
        int sumFieldCode3730Two = 100000;
        int sumFieldCode9702Two = 6310;
        int sumFieldCode1119Two = 1017299;
        int sumFieldCode86Two = 0;
        int sumFieldCode85Two = 1017299;
        int sumFieldCode172Two = 783775;
        int sumFieldCode167Two = 1017890;
        int sumFieldCode146Two = 1316195;
        int sumFieldCode72Two = 6600000;
        int sumFieldCode17126Two = 5283805;
        int sumFieldCode158Two = -298306;
        int sumFieldCode153Two = 18;
        int sumFieldCode17130Two = 298324;

        Assert.assertEquals(sumFieldCode219One, fieldsOne.getEiendeler().getSumEiendeler().intValue());
        Assert.assertEquals(sumFieldCode217One, fieldsOne.getEiendeler().getAnleggsmidler().getSumAnleggsmidler().intValue());
        Assert.assertEquals(sumFieldCode194One, fieldsOne.getEiendeler().getOmloepsmidler().getSumOmloepsmidler().intValue());
        Assert.assertEquals(sumFieldCode251One, fieldsOne.getEgenkapitalGjeld().getSumEgenkapitalGjeld().intValue());
        Assert.assertEquals(sumFieldCode250One, fieldsOne.getEgenkapitalGjeld().getEgenkapital().getSumEgenkapital().intValue());
        Assert.assertEquals(sumFieldCode3730One, fieldsOne.getEgenkapitalGjeld().getEgenkapital().getInnskuttEgenkapital().getSumInnskuttEgenkaptial().intValue());
        Assert.assertEquals(sumFieldCode9702One, fieldsOne.getEgenkapitalGjeld().getEgenkapital().getOpptjentEgenkapital().getSumOpptjentEgenkapital().intValue());
        Assert.assertEquals(sumFieldCode1119One, fieldsOne.getEgenkapitalGjeld().getGjeldOversikt().getSumGjeld().intValue());
        Assert.assertEquals(sumFieldCode86One, fieldsOne.getEgenkapitalGjeld().getGjeldOversikt().getLangsiktigGjeld().getSumLangsiktigGjeld().intValue());
        Assert.assertEquals(sumFieldCode85One, fieldsOne.getEgenkapitalGjeld().getGjeldOversikt().getKortsiktigGjeld().getSumKortsiktigGjeld().intValue());
        Assert.assertEquals(sumFieldCode172One, fieldsOne.getResultatregnskapResultat().getAarsresultat().intValue());
        Assert.assertEquals(sumFieldCode36633One, fieldsOne.getResultatregnskapResultat().getTotalresultat().intValue());
        Assert.assertEquals(sumFieldCode167One, fieldsOne.getResultatregnskapResultat().getOrdinaertResultatFoerSkattekostnad().intValue());
        Assert.assertEquals(sumFieldCode146One, fieldsOne.getResultatregnskapResultat().getDriftsresultat().getDriftsresultat().intValue());
        Assert.assertEquals(sumFieldCode72One, fieldsOne.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().getSumDriftsinntekter().intValue());
        Assert.assertEquals(sumFieldCode17126One, fieldsOne.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().getSumDriftskostnad().intValue());
        Assert.assertEquals(sumFieldCode158One, fieldsOne.getResultatregnskapResultat().getFinansresultat().getNettoFinans().intValue());
        Assert.assertEquals(sumFieldCode153One, fieldsOne.getResultatregnskapResultat().getFinansresultat().getFinansinntekt().getSumFinansinntekter().intValue());
        Assert.assertEquals(sumFieldCode17130One, fieldsOne.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getSumFinanskostnad().intValue());

        Assert.assertEquals(sumFieldCode219Two, fieldsTwo.getEiendeler().getSumEiendeler().intValue());
        Assert.assertEquals(sumFieldCode217Two, fieldsTwo.getEiendeler().getAnleggsmidler().getSumAnleggsmidler().intValue());
        Assert.assertEquals(sumFieldCode194Two, fieldsTwo.getEiendeler().getOmloepsmidler().getSumOmloepsmidler().intValue());
        Assert.assertEquals(sumFieldCode251Two, fieldsTwo.getEgenkapitalGjeld().getSumEgenkapitalGjeld().intValue());
        Assert.assertEquals(sumFieldCode250Two, fieldsTwo.getEgenkapitalGjeld().getEgenkapital().getSumEgenkapital().intValue());
        Assert.assertEquals(sumFieldCode3730Two, fieldsTwo.getEgenkapitalGjeld().getEgenkapital().getInnskuttEgenkapital().getSumInnskuttEgenkaptial().intValue());
        Assert.assertEquals(sumFieldCode9702Two, fieldsTwo.getEgenkapitalGjeld().getEgenkapital().getOpptjentEgenkapital().getSumOpptjentEgenkapital().intValue());
        Assert.assertEquals(sumFieldCode1119Two, fieldsTwo.getEgenkapitalGjeld().getGjeldOversikt().getSumGjeld().intValue());
        Assert.assertEquals(sumFieldCode86Two, fieldsTwo.getEgenkapitalGjeld().getGjeldOversikt().getLangsiktigGjeld().getSumLangsiktigGjeld().intValue());
        Assert.assertEquals(sumFieldCode85Two, fieldsTwo.getEgenkapitalGjeld().getGjeldOversikt().getKortsiktigGjeld().getSumKortsiktigGjeld().intValue());
        Assert.assertEquals(sumFieldCode172Two, fieldsTwo.getResultatregnskapResultat().getAarsresultat().intValue());
        Assert.assertNull(fieldsTwo.getResultatregnskapResultat().getTotalresultat());
        Assert.assertEquals(sumFieldCode167Two, fieldsTwo.getResultatregnskapResultat().getOrdinaertResultatFoerSkattekostnad().intValue());
        Assert.assertEquals(sumFieldCode146Two, fieldsTwo.getResultatregnskapResultat().getDriftsresultat().getDriftsresultat().intValue());
        Assert.assertEquals(sumFieldCode72Two, fieldsTwo.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().getSumDriftsinntekter().intValue());
        Assert.assertEquals(sumFieldCode17126Two, fieldsTwo.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().getSumDriftskostnad().intValue());
        Assert.assertEquals(sumFieldCode158Two, fieldsTwo.getResultatregnskapResultat().getFinansresultat().getNettoFinans().intValue());
        Assert.assertEquals(sumFieldCode153Two, fieldsTwo.getResultatregnskapResultat().getFinansresultat().getFinansinntekt().getSumFinansinntekter().intValue());
        Assert.assertEquals(sumFieldCode17130Two, fieldsTwo.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getSumFinanskostnad().intValue());
    }
}
