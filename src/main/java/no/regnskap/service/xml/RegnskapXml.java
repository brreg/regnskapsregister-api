package no.regnskap.service.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapXml {

    @JacksonXmlElementWrapper(useWrapping = false)
    private RegnskapXmlHode hode;

    @JacksonXmlElementWrapper(useWrapping = false)
    private RegnskapXmlInfo[] info;

    public RegnskapXmlHode getHode() {
        return hode;
    }

    public RegnskapXmlInfo[] getInfo() {
        return info;
    }

    public void setHode(RegnskapXmlHode hode) {
        this.hode = hode;
    }

    public void setInfo(RegnskapXmlInfo[] info) {
        this.info = info;
    }
}


