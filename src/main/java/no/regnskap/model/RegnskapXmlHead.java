package no.regnskap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapXmlHead {

    private String orgnr;

    private String regnskapstype;

    private Integer regnaar;

    private String oppstillingsplanVersjonsnr;

    private String valutakode;

    private String regnskap_dokumenttype;

    private String startdato;

    private String avslutningsdato;

    private String mottakstype;

    private String avviklingsregnskap;

    private String feilvaloer;

    private String journalnr;

    private String mottattDato;

    private String orgform;

    @JacksonXmlProperty(localName = "mor_i_konsern")
    private String morselskap;

    private String reglerSmaa;

    private String fleksiblePoster;

    private String fravalgRevisjon;

    private String utarbeidetRegnskapsforer;

    private String bistandRegnskapsforer;

    private String aarsregnskapstype;

    private String landForLand;

    private String revisorberetningIkkeLevert;

    private String ifrsSelskap;

    private String forenkletIfrsSelskap;

    private String ifrsKonsern;

    private String forenkletIfrsKonsern;


    public String getOrgnr() {
        return orgnr;
    }

    public void setOrgnr(final String orgnr) {
        this.orgnr = orgnr;
    }

    public String getRegnskapstype() {
        return regnskapstype;
    }

    public void setRegnskapstype(final String regnskapstype) {
        this.regnskapstype = regnskapstype;
    }

    public Integer getRegnaar() {
        return regnaar;
    }

    public void setRegnaar(final Integer regnaar) {
        this.regnaar = regnaar;
    }

    public String getOppstillingsplanVersjonsnr() {
        return oppstillingsplanVersjonsnr;
    }

    public void setOppstillingsplanVersjonsnr(final String oppstillingsplanVersjonsnr) {
        this.oppstillingsplanVersjonsnr = oppstillingsplanVersjonsnr;
    }

    public String getRegnskapDokumenttype() {
        return regnskap_dokumenttype;
    }

    public void setRegnskapDokumenttype(final String regnskap_dokumenttype) {
        this.regnskap_dokumenttype = regnskap_dokumenttype;
    }

    public String getValutakode() {
        return valutakode;
    }

    public void setValutakode(final String valutakode) {
        this.valutakode = valutakode;
    }

    public String getStartdato() {
        return startdato;
    }

    public void setStartdato(final String startdato) {
        this.startdato = startdato;
    }

    public String getAvslutningsdato() {
        return avslutningsdato;
    }

    public void setAvslutningsdato(final String avslutningsdato) {
        this.avslutningsdato = avslutningsdato;
    }

    public String getMottakstype() {
        return mottakstype;
    }

    public void setMottakstype(final String mottakstype) {
        this.mottakstype = mottakstype;
    }

    public String getAvviklingsregnskap() {
        return avviklingsregnskap;
    }

    public void setAvviklingsregnskap(final String avviklingsregnskap) {
        this.avviklingsregnskap = avviklingsregnskap;
    }

    public String getFeilvaloer() {
        return feilvaloer;
    }

    public void setFeilvaloer(final String feilvaloer) {
        this.feilvaloer = feilvaloer;
    }

    public String getJournalnr() {
        return journalnr;
    }

    public void setJournalnr(final String journalnr) {
        this.journalnr = journalnr;
    }

    public String getMottattDato() {
        return mottattDato;
    }

    public void setMottattDato(final String mottattDato) {
        this.mottattDato = mottattDato;
    }

    public String getOrgform() {
        return orgform;
    }

    public void setOrgform(final String orgform) {
        this.orgform = orgform;
    }

    public String getMorselskap() {
        return morselskap;
    }

    public void setMorselskap(final String morselskap) {
        this.morselskap = morselskap;
    }

    public String getReglerSmaa() {
        return reglerSmaa;
    }

    public void setReglerSmaa(final String reglerSmaa) {
        this.reglerSmaa = reglerSmaa;
    }

    public String getFleksiblePoster() {
        return fleksiblePoster;
    }

    public void setFleksiblePoster(final String fleksiblePoster) {
        this.fleksiblePoster = fleksiblePoster;
    }

    public String getFravalgRevisjon() {
        return fravalgRevisjon;
    }

    public void setFravalgRevisjon(final String fravalgRevisjon) {
        this.fravalgRevisjon = fravalgRevisjon;
    }

    public String getUtarbeidetRegnskapsforer() {
        return utarbeidetRegnskapsforer;
    }

    public void setUtarbeidetRegnskapsforer(final String utarbeidetRegnskapsforer) {
        this.utarbeidetRegnskapsforer = utarbeidetRegnskapsforer;
    }

    public String getBistandRegnskapsforer() {
        return bistandRegnskapsforer;
    }

    public void setBistandRegnskapsforer(final String bistandRegnskapsforer) {
        this.bistandRegnskapsforer = bistandRegnskapsforer;
    }

    public String getAarsregnskapstype() {
        return aarsregnskapstype;
    }

    public void setAarsregnskapstype(final String aarsregnskapstype) {
        this.aarsregnskapstype = aarsregnskapstype;
    }

    public String getLandForLand() {
        return landForLand;
    }

    public void setLandForLand(final String landForLand) {
        this.landForLand = landForLand;
    }

    public String getRevisorberetningIkkeLevert() {
        return revisorberetningIkkeLevert;
    }

    public void setRevisorberetningIkkeLevert(final String revisorberetningIkkeLevert) {
        this.revisorberetningIkkeLevert = revisorberetningIkkeLevert;
    }

    public String getIfrsSelskap() {
        return ifrsSelskap;
    }

    public void setIfrsSelskap(final String ifrsSelskap) {
        this.ifrsSelskap = ifrsSelskap;
    }

    public String getForenkletIfrsSelskap() {
        return forenkletIfrsSelskap;
    }

    public void setForenkletIfrsSelskap(final String forenkletIfrsSelskap) {
        this.forenkletIfrsSelskap = forenkletIfrsSelskap;
    }

    public String getIfrsKonsern() {
        return ifrsKonsern;
    }

    public void setIfrsKonsern(final String ifrsKonsern) {
        this.ifrsKonsern = ifrsKonsern;
    }

    public String getForenkletIfrsKonsern() {
        return forenkletIfrsKonsern;
    }

    public void setForenkletIfrsKonsern(final String forenkletIfrsKonsern) {
        this.forenkletIfrsKonsern = forenkletIfrsKonsern;
    }

}