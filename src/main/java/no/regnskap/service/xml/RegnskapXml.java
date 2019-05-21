package no.regnskap.service.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapXml {

    @JacksonXmlProperty(localName = "hode")
    @JacksonXmlElementWrapper(useWrapping = false)
    private RegnskapXmlHead head;

    @JacksonXmlProperty(localName = "info")
    @JacksonXmlElementWrapper(useWrapping = false)
    private RegnskapXmlInfo[] posts;

    public RegnskapXmlHead getHead() {
        return head;
    }

    public void setHead(RegnskapXmlHead head) {
        this.head = head;
    }

    public RegnskapXmlInfo[] getPosts() {
        return posts;
    }

    public void setPosts(RegnskapXmlInfo[] posts) {
        this.posts = posts;
    }
}


