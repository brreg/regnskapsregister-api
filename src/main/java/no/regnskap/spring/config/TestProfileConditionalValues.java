package no.regnskap.spring.config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Service
@Profile("test")
class TestProfileConditionalValues implements ProfileConditionalValues {
    private static final String REGNSKAPSREGISTERET_URL = "http://invalid.org/regnskap/";
    private static final String ORGANIZATION_CATALOGUE_URL = "https://invalid.org/organizations/";

    @Override
    public String regnskapsregisteretUrl() {
        return REGNSKAPSREGISTERET_URL;
    }

    @Override
    public String organizationCatalogueUrl() {
        return ORGANIZATION_CATALOGUE_URL;
    }
}
