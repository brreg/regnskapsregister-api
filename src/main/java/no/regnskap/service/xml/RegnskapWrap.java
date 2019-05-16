package no.regnskap.service.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.springframework.data.annotation.Id;

@JacksonXmlRootElement(localName = "deler")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapWrap {

    @Id
    private String checksum;

    private long created = System.currentTimeMillis();

    @JacksonXmlProperty(localName = "del")
    @JacksonXmlElementWrapper(useWrapping = false)
    private RegnskapXml[] list;

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public long getCreated() {
        return created;
    }

    public RegnskapXml[] getList() {
        return list;
    }

    public void setList(RegnskapXml[] list) {
        this.list = list;
    }
}
