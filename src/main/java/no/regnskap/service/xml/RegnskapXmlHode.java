package no.regnskap.service.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapXmlHode {
    private String orgnr;
    private String regnskapstype;
    private String regnaar;
    private String oppstillingsplan_versjonsnr;
    private String valutakode;
    private String regnskap_dokumenttype;
    private String startdato;
    private String avslutningsdato;
    private String mottakstype;
    private String avviklingsregnskap;
    private String feilvaloer;
    private String journalnr;
    private String mottatt_dato;
    private String orgform;
    private String mor_i_konsern;
    private String regler_smaa;
    private String fleksible_poster;
    private String fravalg_revisjon;
    private String utarbeidet_regnskapsforer;
    private String bistand_regnskapsforer;

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

    public String getRegnaar() {
        return regnaar;
    }

    public void setRegnaar(String regnaar) {
        this.regnaar = regnaar;
    }

    public String getOppstillingsplan_versjonsnr() {
        return oppstillingsplan_versjonsnr;
    }

    public void setOppstillingsplan_versjonsnr(String oppstillingsplan_versjonsnr) {
        this.oppstillingsplan_versjonsnr = oppstillingsplan_versjonsnr;
    }

    public String getValutakode() {
        return valutakode;
    }

    public void setValutakode(String valutakode) {
        this.valutakode = valutakode;
    }

    public String getRegnskap_dokumenttype() {
        return regnskap_dokumenttype;
    }

    public void setRegnskap_dokumenttype(String regnskap_dokumenttype) {
        this.regnskap_dokumenttype = regnskap_dokumenttype;
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

    public String getAvviklingsregnskap() {
        return avviklingsregnskap;
    }

    public void setAvviklingsregnskap(String avviklingsregnskap) {
        this.avviklingsregnskap = avviklingsregnskap;
    }

    public String getFeilvaloer() {
        return feilvaloer;
    }

    public void setFeilvaloer(String feilvaloer) {
        this.feilvaloer = feilvaloer;
    }

    public String getJournalnr() {
        return journalnr;
    }

    public void setJournalnr(String journalnr) {
        this.journalnr = journalnr;
    }

    public String getMottatt_dato() {
        return mottatt_dato;
    }

    public void setMottatt_dato(String mottatt_dato) {
        this.mottatt_dato = mottatt_dato;
    }

    public String getOrgform() {
        return orgform;
    }

    public void setOrgform(String orgform) {
        this.orgform = orgform;
    }

    public String getMor_i_konsern() {
        return mor_i_konsern;
    }

    public void setMor_i_konsern(String mor_i_konsern) {
        this.mor_i_konsern = mor_i_konsern;
    }

    public String getRegler_smaa() {
        return regler_smaa;
    }

    public void setRegler_smaa(String regler_smaa) {
        this.regler_smaa = regler_smaa;
    }

    public String getFleksible_poster() {
        return fleksible_poster;
    }

    public void setFleksible_poster(String fleksible_poster) {
        this.fleksible_poster = fleksible_poster;
    }

    public String getFravalg_revisjon() {
        return fravalg_revisjon;
    }

    public void setFravalg_revisjon(String fravalg_revisjon) {
        this.fravalg_revisjon = fravalg_revisjon;
    }

    public String getUtarbeidet_regnskapsforer() {
        return utarbeidet_regnskapsforer;
    }

    public void setUtarbeidet_regnskapsforer(String utarbeidet_regnskapsforer) {
        this.utarbeidet_regnskapsforer = utarbeidet_regnskapsforer;
    }

    public String getBistand_regnskapsforer() {
        return bistand_regnskapsforer;
    }

    public void setBistand_regnskapsforer(String bistand_regnskapsforer) {
        this.bistand_regnskapsforer = bistand_regnskapsforer;
    }
}