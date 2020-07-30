package no.regnskap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapXmlInfo {

    @JacksonXmlProperty
    private String feltkode;

    @JacksonXmlProperty
    private BigDecimal sum;

    @JacksonXmlProperty
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<RegnskapXmlPost> post;


    public String getFeltkode() {
        return feltkode;
    }

    public void setFeltkode(final String feltkode) {
        this.feltkode = feltkode;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(final BigDecimal sum) {
        this.sum = sum;
    }

    public List<RegnskapXmlPost> getPost() {
        return post;
    }

    public void setPost(final List<RegnskapXmlPost> post) {
        this.post = post;
    }

}
