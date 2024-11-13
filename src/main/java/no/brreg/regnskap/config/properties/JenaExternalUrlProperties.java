package no.brreg.regnskap.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "regnskap.jena-external-url")
public class JenaExternalUrlProperties {

    private String regnskapsregisteretUrl;
    private String organizationCatalogueUrl;

    public String getRegnskapsregisteretUrl() {
        return regnskapsregisteretUrl;
    }

    public void setRegnskapsregisteretUrl(String regnskapsregisteretUrl) {
        this.regnskapsregisteretUrl = regnskapsregisteretUrl;
    }

    public String getOrganizationCatalogueUrl() {
        return organizationCatalogueUrl;
    }

    public void setOrganizationCatalogueUrl(String organizationCatalogueUrl) {
        this.organizationCatalogueUrl = organizationCatalogueUrl;
    }
}
