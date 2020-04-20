package no.regnskap.mapper

import no.regnskap.model.RegnskapFieldsDB
import no.regnskap.model.RegnskapXmlInfo

private const val /*R020*/ FELTKODE_SALGSINNTEKTER        = "1340"
private const val /*R040*/ FELTKODE_DRIFTSINNTEKTER       = "72"
private const val /*R090*/ FELTKODE_LOENNSKOSTNAD         = "81"
private const val /*R130*/ FELTKODE_DRIFTSKOSTNAD         = "17126"
private const val /*R140*/ FELTKODE_DRIFTSRESULTAT        = "146"
private const val /*R220*/ FELTKODE_FINANSINNTEKT         = "153"
private const val /*R270*/ FELTKODE_RENTEKOSTNAD_SAMME_KONSERN = "7037"
private const val /*R280*/ FELTKODE_ANNEN_RENTEKOSTNAD    = "2216"
private const val /*R300*/ FELTKODE_FINANSKOSTNAD         = "17130"
private const val /*R310*/ FELTKODE_FINANSRESULTAT        = "158"
private const val /*R330*/ FELTKODE_RESULTAT_ORDINAERT    = "167"
private const val /*R340*/ FELTKODE_SKATTEKOSTNAD_ORDINÆRT_RESULTAT = "11835"
private const val /*R372*/ FELTKODE_EKSTRAORDINÆRE_POSTER = "29048"
private const val /*R380*/ FELTKODE_SKATTEKOSTNAD_EKSTRAORDINÆRT_RESULTAT = "2821"
private const val /*R400*/ FELTKODE_RESULTAT_AAR          = "172"
private const val /*R409*/ FELTKODE_RESULTAT_TOTAL        = "36633"

private const val /*B060*/ FELTKODE_GOODWILL              = "206"
private const val /*B230*/ FELTKODE_ANLEGGSMIDLER         = "217"
private const val /*B267*/ FELTKODE_SUM_VARER             = "25012"
private const val /*B310*/ FELTKODE_SUM_FORDRINGER        = "80"
private const val /*B380*/ FELTKODE_SUM_INVESTERINGER     = "6601"
private const val /*B401*/ FELTKODE_SUM_BANKINNSKUDD_KONTANTER = "29042"
private const val /*B410*/ FELTKODE_OMLOEPSMIDLER         = "194"
private const val /*B420*/ FELTKODE_EIENDELER             = "219"
private const val /*B490*/ FELTKODE_INNSKUTT_EGENKAPITAL  = "3730"
private const val /*B530*/ FELTKODE_OPPTJENT_EGENKAPITAL  = "9702"
private const val /*B540*/ FELTKODE_EGENKAPITAL           = "250"
private const val /*B650*/ FELTKODE_LANGSIKTIG_GJELD      = "86"
private const val /*B750*/ FELTKODE_KORTSIKTIG_GJELD      = "85"
private const val /*B755*/ FELTKODE_GJELD                 = "1119"
private const val /*B760*/ FELTKODE_EGENKAPITAL_GJELD     = "251"


fun mapFieldsFromXmlData(fields: RegnskapFieldsDB, xmlData: List<RegnskapXmlInfo>): RegnskapFieldsDB {
    xmlData.forEach {
        when (it.feltkode) {
            FELTKODE_GOODWILL                   -> fields.eiendeler.goodwill = it.sum
            FELTKODE_ANLEGGSMIDLER              -> fields.eiendeler.anleggsmidler.sumAnleggsmidler = it.sum
            FELTKODE_SUM_VARER             -> fields.eiendeler.sumVarer = it.sum
            FELTKODE_SUM_FORDRINGER        -> fields.eiendeler.sumFordringer = it.sum
            FELTKODE_SUM_INVESTERINGER     -> fields.eiendeler.sumInvesteringer = it.sum
            FELTKODE_SUM_BANKINNSKUDD_KONTANTER -> fields.eiendeler.sumBankinnskuddOgKontanter = it.sum
            FELTKODE_OMLOEPSMIDLER              -> fields.eiendeler.omloepsmidler.sumOmloepsmidler = it.sum
            FELTKODE_EIENDELER                  -> fields.eiendeler.sumEiendeler = it.sum
            FELTKODE_INNSKUTT_EGENKAPITAL       -> fields.egenkapitalGjeld.egenkapital.innskuttEgenkapital.sumInnskuttEgenkaptial = it.sum
            FELTKODE_OPPTJENT_EGENKAPITAL       -> fields.egenkapitalGjeld.egenkapital.opptjentEgenkapital.sumOpptjentEgenkapital = it.sum
            FELTKODE_EGENKAPITAL                -> fields.egenkapitalGjeld.egenkapital.sumEgenkapital = it.sum
            FELTKODE_LANGSIKTIG_GJELD           -> fields.egenkapitalGjeld.gjeldOversikt.langsiktigGjeld.sumLangsiktigGjeld = it.sum
            FELTKODE_KORTSIKTIG_GJELD           -> fields.egenkapitalGjeld.gjeldOversikt.kortsiktigGjeld.sumKortsiktigGjeld = it.sum
            FELTKODE_GJELD                      -> fields.egenkapitalGjeld.gjeldOversikt.sumGjeld = it.sum
            FELTKODE_EGENKAPITAL_GJELD          -> fields.egenkapitalGjeld.sumEgenkapitalGjeld = it.sum

            FELTKODE_SALGSINNTEKTER             -> fields.resultatregnskapResultat.driftsresultat.driftsinntekter.salgsinntekter = it.sum
            FELTKODE_DRIFTSINNTEKTER            -> fields.resultatregnskapResultat.driftsresultat.driftsinntekter.sumDriftsinntekter = it.sum
            FELTKODE_LOENNSKOSTNAD              -> fields.resultatregnskapResultat.driftsresultat.driftskostnad.loennskostnad = it.sum
            FELTKODE_DRIFTSKOSTNAD              -> fields.resultatregnskapResultat.driftsresultat.driftskostnad.sumDriftskostnad = it.sum
            FELTKODE_DRIFTSRESULTAT             -> fields.resultatregnskapResultat.driftsresultat.driftsresultat = it.sum
            FELTKODE_FINANSINNTEKT              -> fields.resultatregnskapResultat.finansresultat.finansinntekt.sumFinansinntekter = it.sum
            FELTKODE_RENTEKOSTNAD_SAMME_KONSERN -> fields.resultatregnskapResultat.finansresultat.finanskostnad.rentekostnadSammeKonsern = it.sum
            FELTKODE_ANNEN_RENTEKOSTNAD         -> fields.resultatregnskapResultat.finansresultat.finanskostnad.annenRentekostnad = it.sum
            FELTKODE_FINANSKOSTNAD              -> fields.resultatregnskapResultat.finansresultat.finanskostnad.sumFinanskostnad = it.sum
            FELTKODE_FINANSRESULTAT             -> fields.resultatregnskapResultat.finansresultat.nettoFinans = it.sum
            FELTKODE_RESULTAT_ORDINAERT         -> fields.resultatregnskapResultat.ordinaertResultatFoerSkattekostnad = it.sum
            FELTKODE_SKATTEKOSTNAD_ORDINÆRT_RESULTAT -> fields.resultatregnskapResultat.ordinaertResultatSkattekostnad = it.sum
            FELTKODE_EKSTRAORDINÆRE_POSTER -> fields.resultatregnskapResultat.ekstraordinaerePoster = it.sum
            FELTKODE_SKATTEKOSTNAD_EKSTRAORDINÆRT_RESULTAT -> fields.resultatregnskapResultat.skattekostnadEkstraordinaertResultat = it.sum
            FELTKODE_RESULTAT_AAR               -> fields.resultatregnskapResultat.aarsresultat = it.sum
            FELTKODE_RESULTAT_TOTAL             -> fields.resultatregnskapResultat.totalresultat = it.sum
        }
    }

    return fields
}
