package no.regnskap.service.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapXmlPost {

    @JacksonXmlProperty(isAttribute = true)
    private String posttype;

    @JacksonXmlProperty(isAttribute = true)
    private String nr;

    private String tall;
    private String notehenvisning;
    private String fritekst;

    public String getPosttype() {
        return posttype;
    }

    public void setPosttype(String posttype) {
        this.posttype = posttype;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public String getTall() {
        return tall;
    }

    public void setTall(String tall) {
        this.tall = tall;
    }

    public String getNotehenvisning() {
        return notehenvisning;
    }

    public void setNotehenvisning(String notehenvisning) {
        this.notehenvisning = notehenvisning;
    }

    public String getFritekst() {
        return fritekst;
    }

    public void setFritekst(String fritekst) {
        this.fritekst = fritekst;
    }
}
