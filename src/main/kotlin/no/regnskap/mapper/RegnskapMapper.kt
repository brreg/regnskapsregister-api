package no.regnskap.mapper

import no.regnskap.generated.model.Regnskap
import no.regnskap.generated.model.Regnskapsprinsipper
import no.regnskap.generated.model.Revisjon
import no.regnskap.generated.model.Tidsperiode
import no.regnskap.generated.model.Virksomhet
import no.regnskap.model.RegnskapDB
import no.regnskap.model.RegnskapFieldsDB
import no.regnskap.model.RegnskapXml
import no.regnskap.model.RegnskapXmlHead

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val XML_TRUE_STRING = "J"
private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

fun mapXmlListForPersistence(regnskapXml: List<RegnskapXml>): List<RegnskapDB> {
    val toPersist: MutableMap<String, RegnskapDB> = HashMap()

    regnskapXml.forEach {
        if(it.head != null) {
            val key = it.head.orgnr + it.head.regnaar
            val mapped = toPersist.getOrDefault(key, it.head.createRegnskapDB())

            mapped.fields = mapFieldsFromXmlData(mapped.fields, it.posts)
            toPersist[key] = mapped
        }
    }

    return ArrayList(toPersist.values)
}

private fun RegnskapXmlHead.createRegnskapDB(): RegnskapDB {
    val regnskapDB = RegnskapDB()

    regnskapDB.orgnr = orgnr
    regnskapDB.aarsregnskapstype = aarsregnskapstype
    regnskapDB.journalnr = journalnr
    regnskapDB.mottakstype = mottakstype
    regnskapDB.oppstillingsplanVersjonsnr = oppstillingsplanVersjonsnr
    regnskapDB.orgform = orgform
    regnskapDB.regnaar = regnaar
    regnskapDB.regnskapstype = regnskapstype
    regnskapDB.valutakode = valutakode

    regnskapDB.avviklingsregnskap = booleanFromXmlData(avviklingsregnskap)
    regnskapDB.bistandRegnskapsforer = booleanFromXmlData(bistandRegnskapsforer)
    regnskapDB.feilvaloer = booleanFromXmlData(feilvaloer)
    regnskapDB.fleksiblePoster = booleanFromXmlData(fleksiblePoster)
    regnskapDB.fravalgRevisjon = booleanFromXmlData(fravalgRevisjon)
    regnskapDB.landForLand = booleanFromXmlData(landForLand)
    regnskapDB.morselskap = booleanFromXmlData(morselskap)
    regnskapDB.reglerSmaa = booleanFromXmlData(reglerSmaa)
    regnskapDB.utarbeidetRegnskapsforer = booleanFromXmlData(utarbeidetRegnskapsforer)
    regnskapDB.revisorberetningIkkeLevert = booleanFromXmlData(revisorberetningIkkeLevert)

    regnskapDB.avslutningsdato = avslutningsdato.localDateFromXmlDateString()
    regnskapDB.mottattDato = mottattDato.localDateFromXmlDateString()
    regnskapDB.startdato = startdato.localDateFromXmlDateString()

    regnskapDB.fields = RegnskapFieldsDB()

    return regnskapDB
}


private fun booleanFromXmlData(trueOrFalse: String?): Boolean =
    XML_TRUE_STRING == trueOrFalse

fun mapPersistenceToGenerated(persistenceDTO: RegnskapDB): Regnskap =
    Regnskap()
        .id(persistenceDTO.id?.toHexString())
        .avviklingsregnskap(persistenceDTO.avviklingsregnskap)
        .valuta(persistenceDTO.valutakode)
        .oppstillingsplan(Regnskap.OppstillingsplanEnum.fromValue(persistenceDTO.aarsregnskapstype.toLowerCase()))
        .revisjon(
            Revisjon()
                .ikkeRevidertAarsregnskap(persistenceDTO.revisorberetningIkkeLevert))
        .regnskapsperiode(
            Tidsperiode()
                .fraDato(persistenceDTO.startdato)
                .tilDato(persistenceDTO.avslutningsdato))
        .regnkapsprinsipper(
            Regnskapsprinsipper()
                .smaaForetak(persistenceDTO.reglerSmaa)
                .regnskapsregler(null)) //TODO Change to correct value
        .virksomhet(
            Virksomhet()
                .organisasjonsnummer(persistenceDTO.orgnr)
                .organisasjonsform(persistenceDTO.orgform)
                .morselskap(persistenceDTO.morselskap)
                .navn(null)) // TODO Change to correct value
        .egenkapitalGjeld(persistenceDTO.fields.egenkapitalGjeld)
        .eiendeler(persistenceDTO.fields.eiendeler)
        .resultatregnskapResultat(persistenceDTO.fields.resultatregnskapResultat)

private fun String.localDateFromXmlDateString(): LocalDate =
    LocalDate.parse(this, dateTimeFormatter)

