package no.regnskap.model

import no.regnskap.generated.model.EgenkapitalGjeld
import no.regnskap.generated.model.Eiendeler
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
    var eiendeler: Eiendeler? = null,
    var egenkapitalGjeld: EgenkapitalGjeld? = null,
    var resultatregnskapResultat: ResultatregnskapResultat? = null
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