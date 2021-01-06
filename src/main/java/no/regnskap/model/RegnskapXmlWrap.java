package no.regnskap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;


@JacksonXmlRootElement(localName = "deler")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapXmlWrap {

    @JacksonXmlProperty(localName = "del")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<RegnskapXml> list;


    public List<RegnskapXml> getList() {
        return list;
    }

    public void setList(final List<RegnskapXml> list) {
        this.list = list;
    }

}
