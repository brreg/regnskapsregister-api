package no.regnskap.model.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import static no.regnskap.service.UpdateService.TRUE_STRING;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapXmlHead {
    private String orgnr;
    private String regnskapstype;
    private Integer regnaar;
    @JacksonXmlProperty(localName = "oppstillingsplan_versjonsnr")
    private String oppstillingsplanVersjonsnr;
    private String valutakode;
    @JacksonXmlProperty(localName = "regnskap_dokumenttype")
    private String regnskapDokumenttype;
    private String startdato;
    private String avslutningsdato;
    private String mottakstype;
    private boolean avviklingsregnskap;
    private boolean feilvaloer;
    private String journalnr;
    @JacksonXmlProperty(localName = "mottatt_dato")
    private String mottattDato;
    private String orgform;
    @JacksonXmlProperty(localName = "mor_i_konsern")
    private boolean morselskap;
    @JacksonXmlProperty(localName = "regler_smaa")
    private boolean reglerSmaa;
    @JacksonXmlProperty(localName = "fleksible_poster")
    private boolean fleksiblePoster;
    @JacksonXmlProperty(localName = "fravalg_revisjon")
    private boolean fravalgRevisjon;
    @JacksonXmlProperty(localName = "utarbeidet_regnskapsforer")
    private boolean utarbeidetRegnskapsforer;
    @JacksonXmlProperty(localName = "bistand_regnskapsforer")
    private boolean bistandRegnskapsforer;
    private String aarsregnskapstype;
    @JacksonXmlProperty(localName = "land_for_land")
    private boolean landForLand;
    @JacksonXmlProperty(localName = "revisorberetning_ikke_levert")
    private boolean revisorberetningIkkeLevert;

    public String getOrgnr() {
        return orgnr;
    }

    public void setOrgnr(String orgnr) {
        this.orgnr = orgnr;
    }

    public String getRegnskapstype() {
        return regnskapstype;
    }

    public void setRegnskapstype(String regnskapstype) {
        this.regnskapstype = regnskapstype;
    }

    public Integer getRegnaar() {
        return regnaar;
    }

    public void setRegnaar(Integer regnaar) {
        this.regnaar = regnaar;
    }

    public String getOppstillingsplanVersjonsnr() {
        return oppstillingsplanVersjonsnr;
    }

    public void setOppstillingsplanVersjonsnr(String oppstillingsplanVersjonsnr) {
        this.oppstillingsplanVersjonsnr = oppstillingsplanVersjonsnr;
    }

    public String getValutakode() {
        return valutakode;
    }

    public void setValutakode(String valutakode) {
        this.valutakode = valutakode;
    }

    public String getRegnskapDokumenttype() {
        return regnskapDokumenttype;
    }

    public void setRegnskapDokumenttype(String regnskapDokumenttype) {
        this.regnskapDokumenttype = regnskapDokumenttype;
    }

    public String getStartdato() {
        return startdato;
    }

    public void setStartdato(String startdato) {
        this.startdato = startdato;
    }

    public String getAvslutningsdato() {
        return avslutningsdato;
    }

    public void setAvslutningsdato(String avslutningsdato) {
        this.avslutningsdato = avslutningsdato;
    }

    public String getMottakstype() {
        return mottakstype;
    }

    public void setMottakstype(String mottakstype) {
        this.mottakstype = mottakstype;
    }

    public boolean isAvviklingsregnskap() {
        return avviklingsregnskap;
    }

    public void setAvviklingsregnskap(String avviklingsregnskap) {
        this.avviklingsregnskap = TRUE_STRING.equals(avviklingsregnskap);
    }

    public boolean isFeilvaloer() {
        return feilvaloer;
    }

    public void setFeilvaloer(String feilvaloer) {
        this.feilvaloer = TRUE_STRING.equals(feilvaloer);
    }

    public String getJournalnr() {
        return journalnr;
    }

    public void setJournalnr(String journalnr) {
        this.journalnr = journalnr;
    }

    public String getMottattDato() {
        return mottattDato;
    }

    public void setMottattDato(String mottattDato) {
        this.mottattDato = mottattDato;
    }

    public String getOrgform() {
        return orgform;
    }

    public void setOrgform(String orgform) {
        this.orgform = orgform;
    }

    public boolean isMorselskap() {
        return morselskap;
    }

    public void setMorselskap(String morselskap) {
        this.morselskap = TRUE_STRING.equals(morselskap);
    }

    public boolean isReglerSmaa() {
        return reglerSmaa;
    }

    public void setReglerSmaa(String reglerSmaa) {
        this.reglerSmaa = TRUE_STRING.equals(reglerSmaa);
    }

    public boolean isFleksiblePoster() {
        return fleksiblePoster;
    }

    public void setFleksiblePoster(String fleksiblePoster) {
        this.fleksiblePoster = TRUE_STRING.equals(fleksiblePoster);
    }

    public boolean isFravalgRevisjon() {
        return fravalgRevisjon;
    }

    public void setFravalgRevisjon(String fravalgRevisjon) {
        this.fravalgRevisjon = TRUE_STRING.equals(fravalgRevisjon);
    }

    public boolean isUtarbeidetRegnskapsforer() {
        return utarbeidetRegnskapsforer;
    }

    public void setUtarbeidetRegnskapsforer(String utarbeidetRegnskapsforer) {
        this.utarbeidetRegnskapsforer = TRUE_STRING.equals(utarbeidetRegnskapsforer);
    }

    public boolean isBistandRegnskapsforer() {
        return bistandRegnskapsforer;
    }

    public void setBistandRegnskapsforer(String bistandRegnskapsforer) {
        this.bistandRegnskapsforer = TRUE_STRING.equals(bistandRegnskapsforer);
    }

    public String getAarsregnskapstype() {
        return aarsregnskapstype;
    }

    public void setAarsregnskapstype(String aarsregnskapstype) {
        this.aarsregnskapstype = aarsregnskapstype;
    }

    public boolean isLandForLand() {
        return landForLand;
    }

    public void setLandForLand(String landForLand) {
        this.landForLand = TRUE_STRING.equals(landForLand);
    }

    public boolean isRevisorberetningIkkeLevert() {
        return revisorberetningIkkeLevert;
    }

    public void setRevisorberetningIkkeLevert(String revisorberetningIkkeLevert) {
        this.revisorberetningIkkeLevert = TRUE_STRING.equals(revisorberetningIkkeLevert);
    }
}