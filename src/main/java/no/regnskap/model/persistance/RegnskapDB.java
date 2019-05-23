package no.regnskap.model.persistance;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Document("regnskap")
public class RegnskapDB {
    @Id
    private String _id;
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
    private RegnskapFieldsDB fields;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
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

    public boolean isAvviklingsregnskap() {
        return avviklingsregnskap;
    }

    public void setAvviklingsregnskap(boolean avviklingsregnskap) {
        this.avviklingsregnskap = avviklingsregnskap;
    }

    public boolean isFeilvaloer() {
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

    public boolean isMorselskap() {
        return morselskap;
    }

    public void setMorselskap(boolean morselskap) {
        this.morselskap = morselskap;
    }

    public boolean isReglerSmaa() {
        return reglerSmaa;
    }

    public void setReglerSmaa(boolean reglerSmaa) {
        this.reglerSmaa = reglerSmaa;
    }

    public boolean isFleksiblePoster() {
        return fleksiblePoster;
    }

    public void setFleksiblePoster(boolean fleksiblePoster) {
        this.fleksiblePoster = fleksiblePoster;
    }

    public boolean isFravalgRevisjon() {
        return fravalgRevisjon;
    }

    public void setFravalgRevisjon(boolean fravalgRevisjon) {
        this.fravalgRevisjon = fravalgRevisjon;
    }

    public boolean isUtarbeidetRegnskapsforer() {
        return utarbeidetRegnskapsforer;
    }

    public void setUtarbeidetRegnskapsforer(boolean utarbeidetRegnskapsforer) {
        this.utarbeidetRegnskapsforer = utarbeidetRegnskapsforer;
    }

    public boolean isBistandRegnskapsforer() {
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

    public boolean isLandForLand() {
        return landForLand;
    }

    public void setLandForLand(boolean landForLand) {
        this.landForLand = landForLand;
    }

    public boolean isRevisorberetningIkkeLevert() {
        return revisorberetningIkkeLevert;
    }

    public void setRevisorberetningIkkeLevert(boolean revisorberetningIkkeLevert) {
        this.revisorberetningIkkeLevert = revisorberetningIkkeLevert;
    }

    public RegnskapFieldsDB getFields() {
        return fields;
    }

    public void setFields(RegnskapFieldsDB fields) {
        this.fields = fields;
    }
}
