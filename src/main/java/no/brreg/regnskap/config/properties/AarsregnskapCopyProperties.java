package no.brreg.regnskap.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "regnskap.aarsregnskap-copy")
public record AarsregnskapCopyProperties(Boolean enabled, String filepathPrefix) {
}
