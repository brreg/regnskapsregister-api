package no.regnskap.mapper

import no.regnskap.generated.model.*
import no.regnskap.model.RegnskapFieldsDB
import no.regnskap.model.RegnskapXmlInfo

private val FELTKODE_EIENDELER = "219"
private val FELTKODE_ANLEGGSMIDLER = "217"
private val FELTKODE_OMLOEPSMIDLER = "194"
private val FELTKODE_EGENKAPITAL_GJELD = "251"
private val FELTKODE_EGENKAPITAL = "250"
private val FELTKODE_INNSKUTT_EGENKAPITAL = "3730"
private val FELTKODE_OPPTJENT_EGENKAPITAL = "9702"
private val FELTKODE_GJELD = "1119"
private val FELTKODE_LANGSIKTIG_GJELD = "86"
private val FELTKODE_KORTSIKTIG_GJELD = "85"
private val FELTKODE_RESULTAT_AAR = "172"
private val FELTKODE_RESULTAT_TOTAL = "36633"
private val FELTKODE_RESULTAT_ORDINAERT = "167"
private val FELTKODE_DRIFTSRESULTAT = "146"
private val FELTKODE_DRIFTSINNTEKTER = "72"
private val FELTKODE_DRIFTSKOSTNAD = "17126"
private val FELTKODE_FINANSRESULTAT = "158"
private val FELTKODE_FINANSINNTEKT = "153"
private val FELTKODE_FINANSKOSTNAD = "17130"

fun mapFieldsFromXmlData(existingValues: RegnskapFieldsDB?, xmlData: List<RegnskapXmlInfo>): RegnskapFieldsDB {
    val fields = existingValues?.copy() ?: RegnskapFieldsDB()

    for (xml in xmlData) {
        when (xml.feltkode) {
            FELTKODE_EIENDELER -> fields.eiendeler.sumEiendeler = xml.sum
            FELTKODE_ANLEGGSMIDLER -> fields.eiendeler.anleggsmidler.sumAnleggsmidler = xml.sum
            FELTKODE_OMLOEPSMIDLER -> fields.eiendeler.omloepsmidler.sumOmloepsmidler = xml.sum
            FELTKODE_EGENKAPITAL_GJELD -> fields.egenkapitalGjeld.sumEgenkapitalGjeld = xml.sum
            FELTKODE_EGENKAPITAL -> fields.egenkapitalGjeld.egenkapital.sumEgenkapital = xml.sum
            FELTKODE_INNSKUTT_EGENKAPITAL -> fields.egenkapitalGjeld.egenkapital.innskuttEgenkapital.sumInnskuttEgenkaptial = xml.sum
            FELTKODE_OPPTJENT_EGENKAPITAL -> fields.egenkapitalGjeld.egenkapital.opptjentEgenkapital.sumOpptjentEgenkapital = xml.sum
            FELTKODE_GJELD -> fields.egenkapitalGjeld.gjeldOversikt.sumGjeld = xml.sum
            FELTKODE_LANGSIKTIG_GJELD -> fields.egenkapitalGjeld.gjeldOversikt.langsiktigGjeld.sumLangsiktigGjeld = xml.sum
            FELTKODE_KORTSIKTIG_GJELD -> fields.egenkapitalGjeld.gjeldOversikt.kortsiktigGjeld.sumKortsiktigGjeld = xml.sum
            FELTKODE_RESULTAT_AAR -> fields.resultatregnskapResultat.aarsresultat = xml.sum
            FELTKODE_RESULTAT_TOTAL -> fields.resultatregnskapResultat.totalresultat = xml.sum
            FELTKODE_RESULTAT_ORDINAERT -> fields.resultatregnskapResultat.ordinaertResultatFoerSkattekostnad = xml.sum
            FELTKODE_DRIFTSRESULTAT -> fields.resultatregnskapResultat.driftsresultat.driftsresultat = xml.sum
            FELTKODE_DRIFTSINNTEKTER -> fields.resultatregnskapResultat.driftsresultat.driftsinntekter.sumDriftsinntekter = xml.sum
            FELTKODE_DRIFTSKOSTNAD -> fields.resultatregnskapResultat.driftsresultat.driftskostnad.sumDriftskostnad = xml.sum
            FELTKODE_FINANSRESULTAT -> fields.resultatregnskapResultat.finansresultat.nettoFinans = xml.sum
            FELTKODE_FINANSINNTEKT -> fields.resultatregnskapResultat.finansresultat.finansinntekt.sumFinansinntekter = xml.sum
            FELTKODE_FINANSKOSTNAD -> fields.resultatregnskapResultat.finansresultat.finanskostnad.sumFinanskostnad = xml.sum
        }
    }

    return fields
}
