package no.regnskap.service.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "deler")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListeRegnskapXml {

    @JacksonXmlProperty(localName = "ant_poster")
    private Integer antallPoster;

    @JacksonXmlProperty(localName = "del")
    @JacksonXmlElementWrapper(useWrapping = false)
    private RegnskapXml[] deler;

    public Integer getAntallPoster() {
        return antallPoster;
    }

    public void setAntallPoster(Integer antallPoster) {
        this.antallPoster = antallPoster;
    }

    public RegnskapXml[] getDeler() {
        return deler;
    }

    public void setDeler(RegnskapXml[] deler) {
        this.deler = deler;
    }
}
