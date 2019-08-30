package no.regnskap.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Document("regnskap")
public class RegnskapDB {
    @Id
    private ObjectId id;
    @Indexed
    private String orgnr;
    private String regnskapstype;
    private Integer regnaar;
    @Field("oppstillingsplan_versjonsnr")
    private String oppstillingsplanVersjonsnr;
    private String valutakode;
    private LocalDate startdato;
    private LocalDate avslutningsdato;
    private String mottakstype;
    private boolean avviklingsregnskap;
    private boolean feilvaloer;
    private String journalnr;
    @Field("mottatt_dato")
    private LocalDate mottattDato;
    private String orgform;
    @Field("mor_i_konsern")
    private boolean morselskap;
    @Field("regler_smaa")
    private boolean reglerSmaa;
    @Field("fleksible_poster")
    private boolean fleksiblePoster;
    @Field("fravalg_revisjon")
    private boolean fravalgRevisjon;
    @Field("utarbeidet_regnskapsforer")
    private boolean utarbeidetRegnskapsforer;
    @Field("bistand_regnskapsforer")
    private boolean bistandRegnskapsforer;
    private String aarsregnskapstype;
    @Field("land_for_land")
    private boolean landForLand;
    @Field("revisorberetning_ikke_levert")
    private boolean revisorberetningIkkeLevert;
    @Field("ifrs_selskap")
    private boolean ifrsSelskap;
    @Field("forenklet_ifrs_selskap")
    private boolean forenkletIfrsSelskap;
    @Field("ifrs_konsern")
    private boolean ifrsKonsern;
    @Field("forenklet_ifrs_konsern")
    private boolean forenkletIfrsKonsern;
    private RegnskapFieldsDB fields;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

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

    public LocalDate getStartdato() {
        return startdato;
    }

    public void setStartdato(LocalDate startdato) {
        this.startdato = startdato;
    }

    public LocalDate getAvslutningsdato() {
        return avslutningsdato;
    }

    public void setAvslutningsdato(LocalDate avslutningsdato) {
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

    public void setAvviklingsregnskap(boolean avviklingsregnskap) {
        this.avviklingsregnskap = avviklingsregnskap;
    }

    public boolean getFeilvaloer() {
        return feilvaloer;
    }

    public void setFeilvaloer(boolean feilvaloer) {
        this.feilvaloer = feilvaloer;
    }

    public String getJournalnr() {
        return journalnr;
    }

    public void setJournalnr(String journalnr) {
        this.journalnr = journalnr;
    }

    public LocalDate getMottattDato() {
        return mottattDato;
    }

    public void setMottattDato(LocalDate mottattDato) {
        this.mottattDato = mottattDato;
    }

    public String getOrgform() {
        return orgform;
    }

    public void setOrgform(String orgform) {
        this.orgform = orgform;
    }

    public boolean getMorselskap() {
        return morselskap;
    }

    public void setMorselskap(boolean morselskap) {
        this.morselskap = morselskap;
    }

    public boolean getReglerSmaa() {
        return reglerSmaa;
    }

    public void setReglerSmaa(boolean reglerSmaa) {
        this.reglerSmaa = reglerSmaa;
    }

    public boolean getFleksiblePoster() {
        return fleksiblePoster;
    }

    public void setFleksiblePoster(boolean fleksiblePoster) {
        this.fleksiblePoster = fleksiblePoster;
    }

    public boolean getFravalgRevisjon() {
        return fravalgRevisjon;
    }

    public void setFravalgRevisjon(boolean fravalgRevisjon) {
        this.fravalgRevisjon = fravalgRevisjon;
    }

    public boolean getUtarbeidetRegnskapsforer() {
        return utarbeidetRegnskapsforer;
    }

    public void setUtarbeidetRegnskapsforer(boolean utarbeidetRegnskapsforer) {
        this.utarbeidetRegnskapsforer = utarbeidetRegnskapsforer;
    }

    public boolean getBistandRegnskapsforer() {
        return bistandRegnskapsforer;
    }

    public void setBistandRegnskapsforer(boolean bistandRegnskapsforer) {
        this.bistandRegnskapsforer = bistandRegnskapsforer;
    }

    public String getAarsregnskapstype() {
        return aarsregnskapstype;
    }

    public void setAarsregnskapstype(String aarsregnskapstype) {
        this.aarsregnskapstype = aarsregnskapstype;
    }

    public boolean getLandForLand() {
        return landForLand;
    }

    public void setLandForLand(boolean landForLand) {
        this.landForLand = landForLand;
    }

    public boolean getRevisorberetningIkkeLevert() {
        return revisorberetningIkkeLevert;
    }

    public void setRevisorberetningIkkeLevert(boolean revisorberetningIkkeLevert) {
        this.revisorberetningIkkeLevert = revisorberetningIkkeLevert;
    }

    public boolean getIfrsSelskap() {
        return ifrsSelskap;
    }

    public void setIfrsSelskap(boolean ifrsSelskap) {
        this.ifrsSelskap = ifrsSelskap;
    }

    public boolean getForenkletIfrsSelskap() {
        return forenkletIfrsSelskap;
    }

    public void setForenkletIfrsSelskap(boolean forenkletIfrsSelskap) {
        this.forenkletIfrsSelskap = forenkletIfrsSelskap;
    }

    public boolean getIfrsKonsern() {
        return ifrsKonsern;
    }

    public void setIfrsKonsern(boolean ifrsKonsern) {
        this.ifrsKonsern = ifrsKonsern;
    }

    public boolean getForenkletIfrsKonsern() {
        return forenkletIfrsKonsern;
    }

    public void setForenkletIfrsKonsern(boolean forenkletIfrsKonsern) {
        this.forenkletIfrsKonsern = forenkletIfrsKonsern;
    }

    public RegnskapFieldsDB getFields() {
        return fields;
    }

    public void setFields(RegnskapFieldsDB fields) {
        this.fields = fields;
    }
}
