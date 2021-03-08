package no.brreg.regnskap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapXml {

    @JacksonXmlProperty(localName = "hode")
    @JacksonXmlElementWrapper(useWrapping = false)
    private RegnskapXmlHead head;

    @JacksonXmlProperty(localName = "info")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<RegnskapXmlInfo> posts;

    public RegnskapXmlHead getHead() {
        return head;
    }

    public void setHead(final RegnskapXmlHead head) {
        this.head = head;
    }

    public List<RegnskapXmlInfo> getPosts() {
        return posts;
    }

    public void setPosts(final List<RegnskapXmlInfo> posts) {
        this.posts = posts;
    }

    public boolean essentialFieldsIncluded() {
        return head!=null && head.getOrgnr()!=null &&
               head.getAvslutningsdato()!=null && head.getStartdato()!=null && head.getJournalnr()!=null;
    }

    public String getReference() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append((head!=null && head.getOrgnr()!=null) ? head.getOrgnr() : "<missing>");
        sb.append(':');
        sb.append((head!=null && head.getJournalnr()!=null) ? head.getJournalnr() : "<missing>");
        sb.append(']');
        return sb.toString();
    }
}
