package no.regnskap.model.dbo;

import no.regnskap.generated.model.Regnskapstype;
import no.regnskap.model.RegnskapFields;

import java.time.LocalDate;


public class Regnskap {
    public static final String REGNSKAPSTYPE_SELSKAP = "s";
    public static final String REGNSKAPSTYPE_KONSERN = "k";

    private Integer id;
    private String orgnr;
    private String regnskapstype;
    private Integer regnaar;
    private String oppstillingsplanVersjonsnr;
    private String valutakode;
    private LocalDate startdato;
    private LocalDate avslutningsdato;
    private String mottakstype;
    private boolean avviklingsregnskap;
    private boolean feilvaloer;
    private String journalnr;
    private LocalDate mottattDato;
    private String orgform;
    private boolean morselskap;
    private boolean reglerSmaa;
    private boolean fleksiblePoster;
    private boolean fravalgRevisjon;
    private boolean utarbeidetRegnskapsforer;
    private boolean bistandRegnskapsforer;
    private String aarsregnskapstype;
    private boolean landForLand;
    private boolean revisorberetningIkkeLevert;
    private boolean ifrsSelskap;
    private boolean forenkletIfrsSelskap;
    private boolean ifrsKonsern;
    private boolean forenkletIfrsKonsern;
    private RegnskapFields fields;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrgnr() {
        return orgnr;
    }

    public void setOrgnr(String orgnr) {
        this.orgnr = orgnr;
    }

    public Regnskapstype getRegnskapstype() {
        return regnskapstypeFromString(this.regnskapstype);
    }

    public void setRegnskapstype(Regnskapstype regnskapstype) {
        this.regnskapstype = Regnskap.regnskapstypeToString(regnskapstype);
    }

    public static Regnskapstype regnskapstypeFromString(final String regnskapstype) {
        if (REGNSKAPSTYPE_SELSKAP.equalsIgnoreCase(regnskapstype)) {
            return Regnskapstype.SELSKAP;
        } else if (REGNSKAPSTYPE_KONSERN.equalsIgnoreCase(regnskapstype)) {
            return Regnskapstype.KONSERN;
        } else {
            return null;
        }
    }

    public static String regnskapstypeToString(final Regnskapstype regnskapstype) {
        if (Regnskapstype.SELSKAP == regnskapstype) {
            return REGNSKAPSTYPE_SELSKAP;
        } else if (Regnskapstype.KONSERN == regnskapstype) {
            return REGNSKAPSTYPE_KONSERN;
        } else {
            return null;
        }
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

    public RegnskapFields getFields() {
        return fields;
    }

    public void setFields(RegnskapFields fields) {
        this.fields = fields;
    }

    public boolean essentialFieldsIncluded() {
        return orgnr!=null && avslutningsdato!=null && startdato!=null && journalnr!=null;
    }
}
