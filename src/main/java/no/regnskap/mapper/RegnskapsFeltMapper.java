package no.regnskap.mapper;

import no.regnskap.generated.model.*;
import no.regnskap.model.persistance.RegnskapFelt;
import no.regnskap.model.xml.RegnskapXmlInfo;

class RegnskapsFeltMapper {
    private final static String FELTKODE_EIENDELER = "219";
    private final static String FELTKODE_ANLEGGSMIDLER = "217";
    private final static String FELTKODE_OMLOEPSMIDLER = "194";
    private final static String FELTKODE_EGENKAPITAL_GJELD = "251";
    private final static String FELTKODE_EGENKAPITAL = "250";
    private final static String FELTKODE_INNSKUTT_EGENKAPITAL = "3730";
    private final static String FELTKODE_OPPTJENT_EGENKAPITAL = "9702";
    private final static String FELTKODE_GJELD = "1119";
    private final static String FELTKODE_LANGSIKTIG_GJELD = "86";
    private final static String FELTKODE_KORTSIKTIG_GJELD = "85";
    private final static String FELTKODE_RESULTAT_AAR = "172";
    private final static String FELTKODE_RESULTAT_TOTAL = "36633";
    private final static String FELTKODE_RESULTAT_ORDINAERT = "167";
    private final static String FELTKODE_DRIFTSRESULTAT = "146";
    private final static String FELTKODE_DRIFTSINNTEKTER = "72";
    private final static String FELTKODE_DRIFTSKOSTNAD = "17126";
    private final static String FELTKODE_FINANSRESULTAT = "158";
    private final static String FELTKODE_FINANSINNTEKT = "153";
    private final static String FELTKODE_FINANSKOSTNAD = "17130";


    static RegnskapFelt mapFieldsFromXmlData(RegnskapFelt felter, RegnskapXmlInfo[] xmlData) {
        Eiendeler eiendeler;
        Anleggsmidler anleggsmidler;
        Omloepsmidler omloepsmidler;
        EgenkapitalGjeld egenkapitalGjeld;
        Egenkapital egenkapital;
        InnskuttEgenkapital innskuttEgenkapital;
        OpptjentEgenkapital opptjentEgenkapital;
        Gjeld gjeld;
        LangsiktigGjeld langsiktigGjeld;
        KortsiktigGjeld kortsiktigGjeld;
        ResultatregnskapResultat resultatregnskapResultat;
        Driftsresultat driftsresultat;
        Driftsinntekter driftsinntekter;
        Driftskostnad driftskostnad;
        Finansresultat finansresultat;
        Finansinntekt finansinntekt;
        Finanskostnad finanskostnad;

        if (felter == null){
            felter = new RegnskapFelt();
            eiendeler = new Eiendeler();
            anleggsmidler = new Anleggsmidler();
            omloepsmidler = new Omloepsmidler();
            egenkapitalGjeld = new EgenkapitalGjeld();
            egenkapital = new Egenkapital();
            innskuttEgenkapital = new InnskuttEgenkapital();
            opptjentEgenkapital = new OpptjentEgenkapital();
            gjeld = new Gjeld();
            langsiktigGjeld = new LangsiktigGjeld();
            kortsiktigGjeld = new KortsiktigGjeld();
            resultatregnskapResultat = new ResultatregnskapResultat();
            driftsresultat = new Driftsresultat();
            driftsinntekter = new Driftsinntekter();
            driftskostnad = new Driftskostnad();
            finansresultat = new Finansresultat();
            finansinntekt = new Finansinntekt();
            finanskostnad = new Finanskostnad();
        } else {
            eiendeler = felter.getEiendeler();
            anleggsmidler = eiendeler.getAnleggsmidler();
            omloepsmidler = eiendeler.getOmloepsmidler();
            egenkapitalGjeld = felter.getEgenkapitalGjeld();
            egenkapital = egenkapitalGjeld.getEgenkapital();
            innskuttEgenkapital = egenkapital.getInnskuttEgenkapital();
            opptjentEgenkapital = egenkapital.getOpptjentEgenkapital();
            gjeld = egenkapitalGjeld.getGjeldOversikt();
            langsiktigGjeld = gjeld.getLangsiktigGjeld();
            kortsiktigGjeld = gjeld.getKortsiktigGjeld();
            resultatregnskapResultat = felter.getResultatregnskapResultat();
            driftsresultat = resultatregnskapResultat.getDriftsresultat();
            driftsinntekter = driftsresultat.getDriftsinntekter();
            driftskostnad = driftsresultat.getDriftskostnad();
            finansresultat = resultatregnskapResultat.getFinansresultat();
            finansinntekt = finansresultat.getFinansinntekt();
            finanskostnad = finansresultat.getFinanskostnad();
        }

        for (RegnskapXmlInfo xml : xmlData) {
            switch (xml.getFeltkode()) {
                case FELTKODE_EIENDELER:
                    eiendeler.setSumEiendeler(xml.getSum());
                    break;
                case FELTKODE_ANLEGGSMIDLER:
                    anleggsmidler.setSumAnleggsmidler(xml.getSum());
                    break;
                case FELTKODE_OMLOEPSMIDLER:
                    omloepsmidler.setSumOmloepsmidler(xml.getSum());
                    break;
                case FELTKODE_EGENKAPITAL_GJELD:
                    egenkapitalGjeld.setSumEgenkapitalGjeld(xml.getSum());
                    break;
                case FELTKODE_EGENKAPITAL:
                    egenkapital.setSumEgenkapital(xml.getSum());
                    break;
                case FELTKODE_INNSKUTT_EGENKAPITAL:
                    innskuttEgenkapital.setSumInnskuttEgenkaptial(xml.getSum());
                    break;
                case FELTKODE_OPPTJENT_EGENKAPITAL:
                    opptjentEgenkapital.setSumOpptjentEgenkapital(xml.getSum());
                    break;
                case FELTKODE_GJELD:
                    gjeld.setSumGjeld(xml.getSum());
                    break;
                case FELTKODE_LANGSIKTIG_GJELD:
                    langsiktigGjeld.setSumLangsiktigGjeld(xml.getSum());
                    break;
                case FELTKODE_KORTSIKTIG_GJELD:
                    kortsiktigGjeld.setSumKortsiktigGjeld(xml.getSum());
                    break;
                case FELTKODE_RESULTAT_AAR:
                    resultatregnskapResultat.setAarsresultat(xml.getSum());
                    break;
                case FELTKODE_RESULTAT_TOTAL:
                    resultatregnskapResultat.setTotalresultat(xml.getSum());
                    break;
                case FELTKODE_RESULTAT_ORDINAERT:
                    resultatregnskapResultat.setOrdinaertResultatFoerSkattekostnad(xml.getSum());
                    break;
                case FELTKODE_DRIFTSRESULTAT:
                    driftsresultat.setDriftsresultat(xml.getSum());
                    break;
                case FELTKODE_DRIFTSINNTEKTER:
                    driftsinntekter.setSumDriftsinntekter(xml.getSum());
                    break;
                case FELTKODE_DRIFTSKOSTNAD:
                    driftskostnad.setSumDriftskostnad(xml.getSum());
                    break;
                case FELTKODE_FINANSRESULTAT:
                    finansresultat.setNettoFinans(xml.getSum());
                    break;
                case FELTKODE_FINANSINNTEKT:
                    finansinntekt.setSumFinansinntekter(xml.getSum());
                    break;
                case FELTKODE_FINANSKOSTNAD:
                    finanskostnad.setSumFinanskostnad(xml.getSum());
                    break;
            }
        }

        finansresultat.setFinansinntekt(finansinntekt);
        finansresultat.setFinanskostnad(finanskostnad);
        resultatregnskapResultat.setFinansresultat(finansresultat);

        driftsresultat.setDriftsinntekter(driftsinntekter);
        driftsresultat.setDriftskostnad(driftskostnad);
        resultatregnskapResultat.setDriftsresultat(driftsresultat);

        eiendeler.setAnleggsmidler(anleggsmidler);
        eiendeler.setOmloepsmidler(omloepsmidler);

        gjeld.setKortsiktigGjeld(kortsiktigGjeld);
        gjeld.setLangsiktigGjeld(langsiktigGjeld);
        egenkapitalGjeld.setGjeldOversikt(gjeld);

        egenkapital.setInnskuttEgenkapital(innskuttEgenkapital);
        egenkapital.setOpptjentEgenkapital(opptjentEgenkapital);
        egenkapitalGjeld.setEgenkapital(egenkapital);

        felter.setResultatregnskapResultat(resultatregnskapResultat);
        felter.setEiendeler(eiendeler);
        felter.setEgenkapitalGjeld(egenkapitalGjeld);

        return felter;
    }
}
