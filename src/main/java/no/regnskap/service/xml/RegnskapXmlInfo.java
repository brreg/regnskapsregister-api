package no.regnskap.service.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapXmlInfo {
    private String feltkode;
    private Long sum;
    private RegnskapXmlPost post;

    public String getFeltkode() {
        return feltkode;
    }

    public Long getSum() {
        return sum;
    }

    public RegnskapXmlPost getPost() {
        return post;
    }

    public void setFeltkode(String feltkode) {
        this.feltkode = feltkode;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum.longValue();
    }

    public void setPost(RegnskapXmlPost post) {
        this.post = post;
    }
}
