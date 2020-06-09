package no.regnskap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;


@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapXmlPost {

    @JacksonXmlProperty(isAttribute = true)
    private String posttype;

    @JacksonXmlProperty(isAttribute = true)
    private String nr;

    @JacksonXmlProperty
    private BigDecimal tall;

    @JacksonXmlProperty
    private String notehenvisning;

    @JacksonXmlProperty
    private String fritekst;


    public String getPosttype() {
        return posttype;
    }

    public void setPosttype(final String posttype) {
        this.posttype = posttype;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(final String nr) {
        this.nr = nr;
    }

    public BigDecimal getTall() {
        return tall;
    }

    public void setTall(final BigDecimal tall) {
        this.tall = tall;
    }

    public String getNotehenvisning() {
        return notehenvisning;
    }

    public void setNotehenvisning(final String notehenvisning) {
        this.notehenvisning = notehenvisning;
    }

    public String getFritekst() {
        return fritekst;
    }

    public void setFritekst(final String fritekst) {
        this.fritekst = fritekst;
    }

}
