package no.regnskap.mapper

import no.regnskap.generated.model.Regnskap
import no.regnskap.generated.model.Regnskapsprinsipper
import no.regnskap.generated.model.Revisjon
import no.regnskap.generated.model.Tidsperiode
import no.regnskap.generated.model.Virksomhet
import no.regnskap.model.RegnskapDB
import no.regnskap.model.RegnskapXml
import no.regnskap.model.RegnskapXmlHead

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val XML_TRUE_STRING = "J"

fun mapXmlListForPersistence(regnskapXml: List<RegnskapXml>): List<RegnskapDB> {
    val toPersist: MutableMap<String, RegnskapDB> = HashMap()
    for (xml in regnskapXml) {
        if(xml.head != null) {
            val key = xml.head.orgnr + xml.head.regnaar
            val mapped = toPersist.getOrDefault(key, xml.head.createRegnskapDB())

            toPersist[key] = mapped.copy(fields = mapFieldsFromXmlData(mapped.fields, xml.posts))
        }
    }

    return ArrayList(toPersist.values)
}

private fun RegnskapXmlHead.createRegnskapDB(): RegnskapDB =
    RegnskapDB(
        orgnr = orgnr,
        aarsregnskapstype = aarsregnskapstype,
        journalnr = journalnr,
        mottakstype = mottakstype,
        oppstillingsplanVersjonsnr = oppstillingsplanVersjonsnr,
        orgform = orgform,
        regnaar = regnaar,
        regnskapstype = regnskapstype,
        valutakode = valutakode,

        avviklingsregnskap = booleanFromXmlData(avviklingsregnskap),
        bistandRegnskapsforer = booleanFromXmlData(bistandRegnskapsforer),
        feilvaloer = booleanFromXmlData(feilvaloer),
        fleksiblePoster = booleanFromXmlData(fleksiblePoster),
        fravalgRevisjon = booleanFromXmlData(fravalgRevisjon),
        landForLand = booleanFromXmlData(landForLand),
        morselskap = booleanFromXmlData(morselskap),
        reglerSmaa = booleanFromXmlData(reglerSmaa),
        utarbeidetRegnskapsforer = booleanFromXmlData(utarbeidetRegnskapsforer),
        revisorberetningIkkeLevert = booleanFromXmlData(revisorberetningIkkeLevert),

        avslutningsdato = localDateFromXmlDateString(avslutningsdato),
        mottattDato = localDateFromXmlDateString(mottattDato),
        startdato = localDateFromXmlDateString(startdato)
    )

private fun booleanFromXmlData(trueOrFalse: String?): Boolean {
    return XML_TRUE_STRING == trueOrFalse
}

fun persistenceToGenerated(persistenceDTO: RegnskapDB): Regnskap {
    return Regnskap()
        .id(persistenceDTO.id)
        .avviklingsregnskap(persistenceDTO.avviklingsregnskap)
        .valuta(persistenceDTO.valutakode)
        .oppstillingsplan(Regnskap.OppstillingsplanEnum.fromValue(persistenceDTO.aarsregnskapstype!!.toLowerCase()))
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
                .regskapsregler(null)) //TODO Change to correct value
        .virksomhet(
            Virksomhet()
                .organisasjonsnummer(persistenceDTO.orgnr)
                .organisasjonsform(persistenceDTO.orgform)
                .morselskap(persistenceDTO.morselskap)
                .levertAarsregnskap(true) // TODO Change to correct value
                .navn(null)) // TODO Change to correct value
        .egenkapitalGjeld(persistenceDTO.fields.egenkapitalGjeld)
        .eiendeler(persistenceDTO.fields.eiendeler)
        .resultatregnskapResultat(persistenceDTO.fields.resultatregnskapResultat)
}

private fun localDateFromXmlDateString(dateString: String?): LocalDate {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    return LocalDate.parse(dateString!!, dateTimeFormatter)
}

