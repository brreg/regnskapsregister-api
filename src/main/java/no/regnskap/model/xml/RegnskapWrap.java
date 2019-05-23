package no.regnskap.model.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import no.regnskap.model.xml.RegnskapXml;

@JacksonXmlRootElement(localName = "deler")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapWrap {

    @JacksonXmlProperty(localName = "del")
    @JacksonXmlElementWrapper(useWrapping = false)
    private RegnskapXml[] list;

    public RegnskapXml[] getList() {
        return list;
    }

    public void setList(RegnskapXml[] list) {
        this.list = list;
    }
}
