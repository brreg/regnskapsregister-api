package no.regnskap.service.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapXml {

    @JacksonXmlProperty(localName = "hode")
    @JacksonXmlElementWrapper(useWrapping = false)
    private RegnskapXmlHode regnskapInformasjon;

    @JacksonXmlProperty(localName = "info")
    @JacksonXmlElementWrapper(useWrapping = false)
    private RegnskapXmlInfo[] postListe;

    public RegnskapXmlHode getRegnskapInformasjon() {
        return regnskapInformasjon;
    }

    public void setRegnskapInformasjon(RegnskapXmlHode regnskapInformasjon) {
        this.regnskapInformasjon = regnskapInformasjon;
    }

    public RegnskapXmlInfo[] getPostListe() {
        return postListe;
    }

    public void setPostListe(RegnskapXmlInfo[] postListe) {
        this.postListe = postListe;
    }
}


