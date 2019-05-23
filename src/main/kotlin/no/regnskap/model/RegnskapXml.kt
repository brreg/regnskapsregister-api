package no.regnskap.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.math.BigDecimal

@JacksonXmlRootElement(localName = "deler")
@JsonIgnoreProperties(ignoreUnknown = true)
class RegnskapXmlWrap {

    @JacksonXmlProperty(localName = "del")
    @JacksonXmlElementWrapper(useWrapping = false)
    val list: List<RegnskapXml> = emptyList()

}

@JsonIgnoreProperties(ignoreUnknown = true)
class RegnskapXml {

    @JacksonXmlProperty(localName = "hode")
    @JacksonXmlElementWrapper(useWrapping = false)
    val head: RegnskapXmlHead? = null

    @JacksonXmlProperty(localName = "info")
    @JacksonXmlElementWrapper(useWrapping = false)
    val posts: List<RegnskapXmlInfo> = emptyList()

}

@JsonIgnoreProperties(ignoreUnknown = true)
class RegnskapXmlInfo {

    @JacksonXmlProperty(localName = "feltkode")
    val feltkode: String? = null

    @JacksonXmlProperty(localName = "sum")
    val sum: BigDecimal? = null

    @JacksonXmlProperty(localName = "post")
    @JacksonXmlElementWrapper(useWrapping = false)
    val post: List<RegnskapXmlPost> = emptyList()

}

@JsonIgnoreProperties(ignoreUnknown = true)
data class RegnskapXmlPost (
    @JacksonXmlProperty(localName = "posttype", isAttribute = true) val posttype: String? = null,
    @JacksonXmlProperty(localName = "nr", isAttribute = true) val nr: String? = null,
    @JacksonXmlProperty(localName = "tall")val tall: BigDecimal? = null,
    @JacksonXmlProperty(localName = "notehenvisning")val notehenvisning: String? = null,
    @JacksonXmlProperty(localName = "fritekst")val fritekst: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RegnskapXmlHead (
    @JacksonXmlProperty(localName = "orgnr") val orgnr: String? = null,
    @JacksonXmlProperty(localName = "regnskapstype") val regnskapstype: String? = null,
    @JacksonXmlProperty(localName = "regnaar") val regnaar: Int? = null,
    @JacksonXmlProperty(localName = "oppstillingsplan_versjonsnr") val oppstillingsplanVersjonsnr: String? = null,
    @JacksonXmlProperty(localName = "valutakode") val valutakode: String? = null,
    @JacksonXmlProperty(localName = "regnskap_dokumenttype") val regnskapDokumenttype: String? = null,
    @JacksonXmlProperty(localName = "startdato") val startdato: String? = null,
    @JacksonXmlProperty(localName = "avslutningsdato") val avslutningsdato: String? = null,
    @JacksonXmlProperty(localName = "mottakstype") val mottakstype: String? = null,
    @JacksonXmlProperty(localName = "avviklingsregnskap") val avviklingsregnskap: String? = null,
    @JacksonXmlProperty(localName = "feilvaloer") val feilvaloer: String? = null,
    @JacksonXmlProperty(localName = "journalnr") val journalnr: String? = null,
    @JacksonXmlProperty(localName = "mottatt_dato") val mottattDato: String? = null,
    @JacksonXmlProperty(localName = "orgform") val orgform: String? = null,
    @JacksonXmlProperty(localName = "mor_i_konsern") val morselskap: String? = null,
    @JacksonXmlProperty(localName = "regler_smaa") val reglerSmaa: String? = null,
    @JacksonXmlProperty(localName = "fleksible_poster") val fleksiblePoster: String? = null,
    @JacksonXmlProperty(localName = "fravalg_revisjon") val fravalgRevisjon: String? = null,
    @JacksonXmlProperty(localName = "utarbeidet_regnskapsforer") val utarbeidetRegnskapsforer: String? = null,
    @JacksonXmlProperty(localName = "bistand_regnskapsforer") val bistandRegnskapsforer: String? = null,
    @JacksonXmlProperty(localName = "aarsregnskapstype") val aarsregnskapstype: String? = null,
    @JacksonXmlProperty(localName = "land_for_land") val landForLand: String? = null,
    @JacksonXmlProperty(localName = "revisorberetning_ikke_levert") val revisorberetningIkkeLevert: String? = null
)