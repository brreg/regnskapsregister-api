package no.regnskap.service.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegnskapXmlInfo {
    private String feltkode;
    private String sum;
    private RegnskapXmlPost post;

    public String getFeltkode() {
        return feltkode;
    }

    public String getSum() {
        return sum;
    }

    public RegnskapXmlPost getPost() {
        return post;
    }

    public void setFeltkode(String feltkode) {
        this.feltkode = feltkode;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public void setPost(RegnskapXmlPost post) {
        this.post = post;
    }
}
