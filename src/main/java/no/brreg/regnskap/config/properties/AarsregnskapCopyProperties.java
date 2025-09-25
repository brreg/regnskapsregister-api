package no.brreg.regnskap.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "regnskap.aarsregnskap-copy")
public record AarsregnskapCopyProperties(Boolean enabled, Boolean externalEnabled, String filepathPrefix, Integer yearsAvailable, Boolean experimentalConverter, Boolean mellombalanseEnabled, String sareptaUrl) {
}
