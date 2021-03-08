package no.brreg.regnskap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;


@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "regnskap.ip.salt")
public class IpProperties {
    private String salt;

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
