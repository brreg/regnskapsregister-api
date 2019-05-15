package no.regnskap.service.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapXmlHode {
    private String orgnr;
    private String regnskapstype;
    private Integer regnaar;
    private String oppstillingsplan_versjonsnr;
    private String valutakode;
    private String regnskap_dokumenttype;
    private String startdato;
    private String avslutningsdato;
    private String mottakstype;
    private boolean avviklingsregnskap;
    private boolean feilvaloer;
    private String journalnr;
    private String mottatt_dato;
    private String orgform;
    private boolean mor_i_konsern;
    private boolean regler_smaa;
    private boolean fleksible_poster;
    private boolean fravalg_revisjon;
    private boolean utarbeidet_regnskapsforer;
    private boolean bistand_regnskapsforer;
    private String aarsregnskapstype;
    private boolean land_for_land;

    private final String booleanTrueString = "J";

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

    public boolean getAvviklingsregnskap() {
        return avviklingsregnskap;
    }

    public void setAvviklingsregnskap(String avviklingsregnskap) {
        this.avviklingsregnskap = booleanTrueString.equals(avviklingsregnskap);
    }

    public boolean getFeilvaloer() {
        return feilvaloer;
    }

    public void setFeilvaloer(String feilvaloer) {
        this.feilvaloer = booleanTrueString.equals(feilvaloer);
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

    public boolean getMor_i_konsern() {
        return mor_i_konsern;
    }

    public void setMor_i_konsern(String mor_i_konsern) {
        this.mor_i_konsern = booleanTrueString.equals(mor_i_konsern);
    }

    public boolean getRegler_smaa() {
        return regler_smaa;
    }

    public void setRegler_smaa(String regler_smaa) {
        this.regler_smaa = booleanTrueString.equals(regler_smaa);
    }

    public boolean getFleksible_poster() {
        return fleksible_poster;
    }

    public void setFleksible_poster(String fleksible_poster) {
        this.fleksible_poster = booleanTrueString.equals(fleksible_poster);
    }

    public boolean getFravalg_revisjon() {
        return fravalg_revisjon;
    }

    public void setFravalg_revisjon(String fravalg_revisjon) {
        this.fravalg_revisjon = booleanTrueString.equals(fravalg_revisjon);
    }

    public boolean getUtarbeidet_regnskapsforer() {
        return utarbeidet_regnskapsforer;
    }

    public void setUtarbeidet_regnskapsforer(String utarbeidet_regnskapsforer) {
        this.utarbeidet_regnskapsforer = booleanTrueString.equals(utarbeidet_regnskapsforer);
    }

    public boolean getBistand_regnskapsforer() {
        return bistand_regnskapsforer;
    }

    public void setBistand_regnskapsforer(String bistand_regnskapsforer) {
        this.bistand_regnskapsforer = booleanTrueString.equals(bistand_regnskapsforer);
    }

    public String getAarsregnskapstype() {
        return aarsregnskapstype;
    }

    public void setAarsregnskapstype(String aarsregnskapstype) {
        this.aarsregnskapstype = aarsregnskapstype;
    }

    public boolean getLand_for_land() {
        return land_for_land;
    }

    public void setLand_for_land(String land_for_land) {
        this.land_for_land = booleanTrueString.equals(land_for_land);
    }
}