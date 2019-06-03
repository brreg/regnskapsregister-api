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

@Document("regnskap_log")
class Checksum (
    @Indexed(unique = true) val checksum: String
) {
    @Id
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