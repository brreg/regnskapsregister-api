package no.brreg.regnskap.mapper;

import no.brreg.regnskap.generated.model.Regnskap;
import no.brreg.regnskap.model.RegnskapFields;
import no.brreg.regnskap.model.RegnskapXmlInfo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RegnskapFieldsMapper {

    public enum RegnskapFieldIncludeMode {
        DEFAULT,
        PARTNER
    }

    private static final String /*R020*/ FELTKODE_SALGSINNTEKTER        = "1340";
    private static final String /*R040*/ FELTKODE_DRIFTSINNTEKTER       = "72";
    private static final String /*R090*/ FELTKODE_LOENNSKOSTNAD         = "81";
    private static final String /*R130*/ FELTKODE_DRIFTSKOSTNAD         = "17126";
    private static final String /*R140*/ FELTKODE_DRIFTSRESULTAT        = "146";
    private static final String /*R220*/ FELTKODE_FINANSINNTEKT         = "153";
    private static final String /*R270*/ FELTKODE_RENTEKOSTNAD_SAMME_KONSERN = "7037";
    private static final String /*R280*/ FELTKODE_ANNEN_RENTEKOSTNAD    = "2216";
    private static final String /*R300*/ FELTKODE_FINANSKOSTNAD         = "17130";
    private static final String /*R310*/ FELTKODE_FINANSRESULTAT        = "158";
    private static final String /*R330*/ FELTKODE_RESULTAT_ORDINAERT    = "167";
    private static final String /*R340*/ FELTKODE_SKATTEKOSTNAD_ORDINÆRT_RESULTAT = "11835";
    private static final String /*R372*/ FELTKODE_EKSTRAORDINÆRE_POSTER = "29048";
    private static final String /*R380*/ FELTKODE_SKATTEKOSTNAD_EKSTRAORDINÆRT_RESULTAT = "2821";
    private static final String /*R400*/ FELTKODE_RESULTAT_AAR          = "172";
    private static final String /*R409*/ FELTKODE_RESULTAT_TOTAL        = "36633";

    private static final String /*B060*/ FELTKODE_GOODWILL              = "206";
    private static final String /*B230*/ FELTKODE_ANLEGGSMIDLER         = "217";
    private static final String /*B267*/ FELTKODE_SUM_VARER             = "25012";
    private static final String /*B310*/ FELTKODE_SUM_FORDRINGER        = "80";
    private static final String /*B380*/ FELTKODE_SUM_INVESTERINGER     = "6601";
    private static final String /*B401*/ FELTKODE_SUM_BANKINNSKUDD_KONTANTER = "29042";
    private static final String /*B410*/ FELTKODE_OMLOEPSMIDLER         = "194";
    private static final String /*B420*/ FELTKODE_EIENDELER             = "219";
    private static final String /*B490*/ FELTKODE_INNSKUTT_EGENKAPITAL  = "3730";
    private static final String /*B530*/ FELTKODE_OPPTJENT_EGENKAPITAL  = "9702";
    private static final String /*B540*/ FELTKODE_EGENKAPITAL           = "250";
    private static final String /*B650*/ FELTKODE_LANGSIKTIG_GJELD      = "86";
    private static final String /*B750*/ FELTKODE_KORTSIKTIG_GJELD      = "85";
    private static final String /*B755*/ FELTKODE_GJELD                 = "1119";
    private static final String /*B760*/ FELTKODE_EGENKAPITAL_GJELD     = "251";


    public static void mapFieldsFromXmlData(final List<RegnskapXmlInfo> xmlData, RegnskapFields fields, final RegnskapFieldIncludeMode mode) {
        switch(mode) {
            case DEFAULT: mapFieldsFromXmlDataDefault(xmlData, fields); break;
            case PARTNER: mapFieldsFromXmlDataPartner(xmlData, fields); break;
        }
    }

    private static void mapFieldsFromXmlDataDefault(final List<RegnskapXmlInfo> xmlData, RegnskapFields fields) {
        mapEgenkapitalGjeldFieldsFromXmlDataDefault(xmlData, fields);
        mapResultatregnskapFieldsFromXmlDataDefault(xmlData, fields);
    }

    private static void mapEgenkapitalGjeldFieldsFromXmlDataDefault(final List<RegnskapXmlInfo> xmlData, RegnskapFields fields) {
        for (RegnskapXmlInfo xmlInfo : xmlData) {
            switch(xmlInfo.getFeltkode()) {
                case FELTKODE_ANLEGGSMIDLER:              fields.getEiendeler().getAnleggsmidler().setSumAnleggsmidler(xmlInfo.getSum()); break;
                case FELTKODE_OMLOEPSMIDLER:              fields.getEiendeler().getOmloepsmidler().setSumOmloepsmidler(xmlInfo.getSum()); break;
                case FELTKODE_EIENDELER:                  fields.getEiendeler().setSumEiendeler(xmlInfo.getSum()); break;
                case FELTKODE_INNSKUTT_EGENKAPITAL:       fields.getEgenkapitalGjeld().getEgenkapital().getInnskuttEgenkapital().setSumInnskuttEgenkaptial(xmlInfo.getSum()); break;
                case FELTKODE_OPPTJENT_EGENKAPITAL:       fields.getEgenkapitalGjeld().getEgenkapital().getOpptjentEgenkapital().setSumOpptjentEgenkapital(xmlInfo.getSum()); break;
                case FELTKODE_EGENKAPITAL:                fields.getEgenkapitalGjeld().getEgenkapital().setSumEgenkapital(xmlInfo.getSum()); break;
                case FELTKODE_LANGSIKTIG_GJELD:           fields.getEgenkapitalGjeld().getGjeldOversikt().getLangsiktigGjeld().setSumLangsiktigGjeld(xmlInfo.getSum()); break;
                case FELTKODE_KORTSIKTIG_GJELD:           fields.getEgenkapitalGjeld().getGjeldOversikt().getKortsiktigGjeld().setSumKortsiktigGjeld(xmlInfo.getSum()); break;
                case FELTKODE_GJELD:                      fields.getEgenkapitalGjeld().getGjeldOversikt().setSumGjeld(xmlInfo.getSum()); break;
                case FELTKODE_EGENKAPITAL_GJELD:          fields.getEgenkapitalGjeld().setSumEgenkapitalGjeld(xmlInfo.getSum()); break;
            }
        }
    }

    private static void mapResultatregnskapFieldsFromXmlDataDefault(final List<RegnskapXmlInfo> xmlData, RegnskapFields fields) {
        for (RegnskapXmlInfo xmlInfo : xmlData) {
            switch(xmlInfo.getFeltkode()) {
                case FELTKODE_DRIFTSINNTEKTER:            fields.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().setSumDriftsinntekter(xmlInfo.getSum()); break;
                case FELTKODE_DRIFTSKOSTNAD:              fields.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().setSumDriftskostnad(xmlInfo.getSum()); break;
                case FELTKODE_DRIFTSRESULTAT:             fields.getResultatregnskapResultat().getDriftsresultat().setDriftsresultat(xmlInfo.getSum()); break;
                case FELTKODE_FINANSINNTEKT:              fields.getResultatregnskapResultat().getFinansresultat().getFinansinntekt().setSumFinansinntekter(xmlInfo.getSum()); break;
                case FELTKODE_FINANSKOSTNAD:              fields.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().setSumFinanskostnad(xmlInfo.getSum()); break;
                case FELTKODE_FINANSRESULTAT:             fields.getResultatregnskapResultat().getFinansresultat().setNettoFinans(xmlInfo.getSum()); break;
                case FELTKODE_RESULTAT_ORDINAERT:         fields.getResultatregnskapResultat().setOrdinaertResultatFoerSkattekostnad(xmlInfo.getSum()); break;
                case FELTKODE_RESULTAT_AAR:               fields.getResultatregnskapResultat().setAarsresultat(xmlInfo.getSum()); break;
                case FELTKODE_RESULTAT_TOTAL:             fields.getResultatregnskapResultat().setTotalresultat(xmlInfo.getSum()); break;
            }
        }
    }

    private static void mapFieldsFromXmlDataPartner(final List<RegnskapXmlInfo> xmlData, RegnskapFields fields) {
        for (RegnskapXmlInfo xmlInfo : xmlData) {
            switch(xmlInfo.getFeltkode()) {
                case FELTKODE_GOODWILL:                   fields.getEiendeler().setGoodwill(xmlInfo.getSum()); break;
                case FELTKODE_ANLEGGSMIDLER:              fields.getEiendeler().getAnleggsmidler().setSumAnleggsmidler(xmlInfo.getSum()); break;
                case FELTKODE_SUM_VARER:                  fields.getEiendeler().setSumVarer(xmlInfo.getSum()); break;
                case FELTKODE_SUM_FORDRINGER:             fields.getEiendeler().setSumFordringer(xmlInfo.getSum()); break;
                case FELTKODE_SUM_INVESTERINGER:          fields.getEiendeler().setSumInvesteringer(xmlInfo.getSum()); break;
                case FELTKODE_SUM_BANKINNSKUDD_KONTANTER: fields.getEiendeler().setSumBankinnskuddOgKontanter(xmlInfo.getSum()); break;
                case FELTKODE_OMLOEPSMIDLER:              fields.getEiendeler().getOmloepsmidler().setSumOmloepsmidler(xmlInfo.getSum()); break;
                case FELTKODE_EIENDELER:                  fields.getEiendeler().setSumEiendeler(xmlInfo.getSum()); break;
                case FELTKODE_INNSKUTT_EGENKAPITAL:       fields.getEgenkapitalGjeld().getEgenkapital().getInnskuttEgenkapital().setSumInnskuttEgenkaptial(xmlInfo.getSum()); break;
                case FELTKODE_OPPTJENT_EGENKAPITAL:       fields.getEgenkapitalGjeld().getEgenkapital().getOpptjentEgenkapital().setSumOpptjentEgenkapital(xmlInfo.getSum()); break;
                case FELTKODE_EGENKAPITAL:                fields.getEgenkapitalGjeld().getEgenkapital().setSumEgenkapital(xmlInfo.getSum()); break;
                case FELTKODE_LANGSIKTIG_GJELD:           fields.getEgenkapitalGjeld().getGjeldOversikt().getLangsiktigGjeld().setSumLangsiktigGjeld(xmlInfo.getSum()); break;
                case FELTKODE_KORTSIKTIG_GJELD:           fields.getEgenkapitalGjeld().getGjeldOversikt().getKortsiktigGjeld().setSumKortsiktigGjeld(xmlInfo.getSum()); break;
                case FELTKODE_GJELD:                      fields.getEgenkapitalGjeld().getGjeldOversikt().setSumGjeld(xmlInfo.getSum()); break;
                case FELTKODE_EGENKAPITAL_GJELD:          fields.getEgenkapitalGjeld().setSumEgenkapitalGjeld(xmlInfo.getSum()); break;

                case FELTKODE_SALGSINNTEKTER:             fields.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().setSalgsinntekter(xmlInfo.getSum()); break;
                case FELTKODE_DRIFTSINNTEKTER:            fields.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().setSumDriftsinntekter(xmlInfo.getSum()); break;
                case FELTKODE_LOENNSKOSTNAD:              fields.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().setLoennskostnad(xmlInfo.getSum()); break;
                case FELTKODE_DRIFTSKOSTNAD:              fields.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().setSumDriftskostnad(xmlInfo.getSum()); break;
                case FELTKODE_DRIFTSRESULTAT:             fields.getResultatregnskapResultat().getDriftsresultat().setDriftsresultat(xmlInfo.getSum()); break;
                case FELTKODE_FINANSINNTEKT:              fields.getResultatregnskapResultat().getFinansresultat().getFinansinntekt().setSumFinansinntekter(xmlInfo.getSum()); break;
                case FELTKODE_RENTEKOSTNAD_SAMME_KONSERN: fields.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().setRentekostnadSammeKonsern(xmlInfo.getSum()); break;
                case FELTKODE_ANNEN_RENTEKOSTNAD:         fields.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().setAnnenRentekostnad(xmlInfo.getSum()); break;
                case FELTKODE_FINANSKOSTNAD:              fields.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().setSumFinanskostnad(xmlInfo.getSum()); break;
                case FELTKODE_FINANSRESULTAT:             fields.getResultatregnskapResultat().getFinansresultat().setNettoFinans(xmlInfo.getSum()); break;
                case FELTKODE_RESULTAT_ORDINAERT:         fields.getResultatregnskapResultat().setOrdinaertResultatFoerSkattekostnad(xmlInfo.getSum()); break;
                case FELTKODE_SKATTEKOSTNAD_ORDINÆRT_RESULTAT: fields.getResultatregnskapResultat().setOrdinaertResultatSkattekostnad(xmlInfo.getSum()); break;
                case FELTKODE_EKSTRAORDINÆRE_POSTER:      fields.getResultatregnskapResultat().setEkstraordinaerePoster(xmlInfo.getSum()); break;
                case FELTKODE_SKATTEKOSTNAD_EKSTRAORDINÆRT_RESULTAT: fields.getResultatregnskapResultat().setSkattekostnadEkstraordinaertResultat(xmlInfo.getSum()); break;
                case FELTKODE_RESULTAT_AAR:               fields.getResultatregnskapResultat().setAarsresultat(xmlInfo.getSum()); break;
                case FELTKODE_RESULTAT_TOTAL:             fields.getResultatregnskapResultat().setTotalresultat(xmlInfo.getSum()); break;
            }
        }
    }

    public static Map<String, BigDecimal> regnskapFields(Regnskap regnskap) {
        Map<String, BigDecimal> resultMap = new HashMap<>();
        putIfNotNull(resultMap, FELTKODE_GOODWILL, regnskap.getEiendeler().getGoodwill());
        putIfNotNull(resultMap, FELTKODE_ANLEGGSMIDLER, regnskap.getEiendeler().getAnleggsmidler().getSumAnleggsmidler());
        putIfNotNull(resultMap, FELTKODE_SUM_VARER, regnskap.getEiendeler().getSumVarer());
        putIfNotNull(resultMap, FELTKODE_SUM_FORDRINGER, regnskap.getEiendeler().getSumFordringer());
        putIfNotNull(resultMap, FELTKODE_SUM_INVESTERINGER, regnskap.getEiendeler().getSumInvesteringer());
        putIfNotNull(resultMap, FELTKODE_SUM_BANKINNSKUDD_KONTANTER, regnskap.getEiendeler().getSumBankinnskuddOgKontanter());
        putIfNotNull(resultMap, FELTKODE_OMLOEPSMIDLER, regnskap.getEiendeler().getOmloepsmidler().getSumOmloepsmidler());
        putIfNotNull(resultMap, FELTKODE_EIENDELER, regnskap.getEiendeler().getSumEiendeler());
        putIfNotNull(resultMap, FELTKODE_INNSKUTT_EGENKAPITAL, regnskap.getEgenkapitalGjeld().getEgenkapital().getInnskuttEgenkapital().getSumInnskuttEgenkaptial());
        putIfNotNull(resultMap, FELTKODE_OPPTJENT_EGENKAPITAL, regnskap.getEgenkapitalGjeld().getEgenkapital().getOpptjentEgenkapital().getSumOpptjentEgenkapital());
        putIfNotNull(resultMap, FELTKODE_EGENKAPITAL, regnskap.getEgenkapitalGjeld().getEgenkapital().getSumEgenkapital());
        putIfNotNull(resultMap, FELTKODE_LANGSIKTIG_GJELD, regnskap.getEgenkapitalGjeld().getGjeldOversikt().getLangsiktigGjeld().getSumLangsiktigGjeld());
        putIfNotNull(resultMap, FELTKODE_KORTSIKTIG_GJELD, regnskap.getEgenkapitalGjeld().getGjeldOversikt().getKortsiktigGjeld().getSumKortsiktigGjeld());
        putIfNotNull(resultMap, FELTKODE_GJELD, regnskap.getEgenkapitalGjeld().getGjeldOversikt().getSumGjeld());
        putIfNotNull(resultMap, FELTKODE_EGENKAPITAL_GJELD, regnskap.getEgenkapitalGjeld().getSumEgenkapitalGjeld());

        putIfNotNull(resultMap, FELTKODE_SALGSINNTEKTER, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().getSalgsinntekter());
        putIfNotNull(resultMap, FELTKODE_DRIFTSINNTEKTER, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().getSumDriftsinntekter());
        putIfNotNull(resultMap, FELTKODE_LOENNSKOSTNAD, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().getLoennskostnad());
        putIfNotNull(resultMap, FELTKODE_DRIFTSKOSTNAD, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().getSumDriftskostnad());
        putIfNotNull(resultMap, FELTKODE_DRIFTSRESULTAT, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsresultat());
        putIfNotNull(resultMap, FELTKODE_FINANSINNTEKT, regnskap.getResultatregnskapResultat().getFinansresultat().getFinansinntekt().getSumFinansinntekter());
        putIfNotNull(resultMap, FELTKODE_RENTEKOSTNAD_SAMME_KONSERN, regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getRentekostnadSammeKonsern());
        putIfNotNull(resultMap, FELTKODE_ANNEN_RENTEKOSTNAD, regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getAnnenRentekostnad());
        putIfNotNull(resultMap, FELTKODE_FINANSKOSTNAD, regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getSumFinanskostnad());
        putIfNotNull(resultMap, FELTKODE_FINANSRESULTAT, regnskap.getResultatregnskapResultat().getFinansresultat().getNettoFinans());
        putIfNotNull(resultMap, FELTKODE_RESULTAT_ORDINAERT, regnskap.getResultatregnskapResultat().getOrdinaertResultatFoerSkattekostnad());
        putIfNotNull(resultMap, FELTKODE_SKATTEKOSTNAD_ORDINÆRT_RESULTAT, regnskap.getResultatregnskapResultat().getOrdinaertResultatSkattekostnad());
        putIfNotNull(resultMap, FELTKODE_EKSTRAORDINÆRE_POSTER, regnskap.getResultatregnskapResultat().getEkstraordinaerePoster());
        putIfNotNull(resultMap, FELTKODE_SKATTEKOSTNAD_EKSTRAORDINÆRT_RESULTAT, regnskap.getResultatregnskapResultat().getSkattekostnadEkstraordinaertResultat());
        putIfNotNull(resultMap, FELTKODE_RESULTAT_AAR, regnskap.getResultatregnskapResultat().getAarsresultat());
        putIfNotNull(resultMap, FELTKODE_RESULTAT_TOTAL, regnskap.getResultatregnskapResultat().getTotalresultat());

        return resultMap;
    }

    private static void putIfNotNull(final Map<String,BigDecimal> map, final String key, final BigDecimal value) {
        if (value != null) {
            map.put(key, value);
        }
    }

}
