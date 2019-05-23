package no.regnskap.model

import no.regnskap.generated.model.*
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate

@Document("regnskap_log")
class Checksum (
    @Indexed(unique = true) val checksum: String
) {
    @Id
    @Field("_id")
    var id: String? = null
}

data class RegnskapFieldsDB (

    val eiendeler: Eiendeler = Eiendeler()
        .anleggsmidler(Anleggsmidler())
        .omloepsmidler(Omloepsmidler()),

    val egenkapitalGjeld: EgenkapitalGjeld = EgenkapitalGjeld()
        .egenkapital(
            Egenkapital()
                .innskuttEgenkapital(InnskuttEgenkapital())
                .opptjentEgenkapital(OpptjentEgenkapital()))
        .gjeldOversikt(
            Gjeld()
                .kortsiktigGjeld(KortsiktigGjeld())
                .langsiktigGjeld(LangsiktigGjeld())),

    val resultatregnskapResultat: ResultatregnskapResultat = ResultatregnskapResultat()
        .driftsresultat(
            Driftsresultat()
                .driftsinntekter(Driftsinntekter())
                .driftskostnad(Driftskostnad()))
        .finansresultat(
            Finansresultat()
                .finansinntekt(Finansinntekt())
                .finanskostnad(Finanskostnad()))

)

@Document("regnskap")
data class RegnskapDB (
    @Indexed var orgnr: String? = null,
    var regnskapstype: String? = null,
    var regnaar: Int? = null,
    var oppstillingsplanVersjonsnr: String? = null,
    var valutakode: String? = null,
    var startdato: LocalDate? = null,
    var avslutningsdato: LocalDate? = null,
    var mottakstype: String? = null,
    var avviklingsregnskap: Boolean = false,
    var feilvaloer: Boolean = false,
    var journalnr: String? = null,
    var mottattDato: LocalDate? = null,
    var orgform: String? = null,
    var morselskap: Boolean = false,
    var reglerSmaa: Boolean = false,
    var fleksiblePoster: Boolean = false,
    var fravalgRevisjon: Boolean = false,
    var utarbeidetRegnskapsforer: Boolean = false,
    var bistandRegnskapsforer: Boolean = false,
    var aarsregnskapstype: String? = null,
    var landForLand: Boolean = false,
    var revisorberetningIkkeLevert: Boolean = false,
    var fields: RegnskapFieldsDB? = null
) {
    @Id
    @Field("_id")
    var id: String? = null
}