package no.brreg.regnskap.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "regnskap.cache")
public record CacheProperties(long ttlAfterAccessedAarRequests, long ttlAfterCreatedAarCopyFilemeta) {
}
