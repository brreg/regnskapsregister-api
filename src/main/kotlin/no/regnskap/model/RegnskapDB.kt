package no.regnskap.model

import no.regnskap.generated.model.Anleggsmidler
import no.regnskap.generated.model.Driftsinntekter
import no.regnskap.generated.model.Driftskostnad
import no.regnskap.generated.model.Driftsresultat
import no.regnskap.generated.model.Egenkapital
import no.regnskap.generated.model.EgenkapitalGjeld
import no.regnskap.generated.model.Eiendeler
import no.regnskap.generated.model.Finansinntekt
import no.regnskap.generated.model.Finanskostnad
import no.regnskap.generated.model.Finansresultat
import no.regnskap.generated.model.Gjeld
import no.regnskap.generated.model.InnskuttEgenkapital
import no.regnskap.generated.model.KortsiktigGjeld
import no.regnskap.generated.model.LangsiktigGjeld
import no.regnskap.generated.model.Omloepsmidler
import no.regnskap.generated.model.OpptjentEgenkapital
import no.regnskap.generated.model.ResultatregnskapResultat
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
    @Indexed val orgnr: String,
    val regnskapstype: String,
    val regnaar: Int,
    val oppstillingsplanVersjonsnr: String,
    val valutakode: String,
    val startdato: LocalDate,
    val avslutningsdato: LocalDate,
    val mottakstype: String,
    val avviklingsregnskap: Boolean,
    val feilvaloer: Boolean,
    val journalnr: String,
    val mottattDato: LocalDate,
    val orgform: String,
    val morselskap: Boolean,
    val reglerSmaa: Boolean,
    val fleksiblePoster: Boolean,
    val fravalgRevisjon: Boolean,
    val utarbeidetRegnskapsforer: Boolean,
    val bistandRegnskapsforer: Boolean,
    val aarsregnskapstype: String,
    val landForLand: Boolean,
    val revisorberetningIkkeLevert: Boolean,
    val fields: RegnskapFieldsDB = RegnskapFieldsDB()
) {
    @Id
    @Field("_id")
    var id: String? = null
}