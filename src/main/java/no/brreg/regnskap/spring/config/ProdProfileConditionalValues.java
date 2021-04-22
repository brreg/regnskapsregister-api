package no.brreg.regnskap.spring.config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Service
@Profile("prod")
class ProdProfileConditionalValues implements ProfileConditionalValues {
    private static final String REGNSKAPSREGISTERET_URL = "http://data.ppe.brreg.no/regnskapsregisteret/regnskap/%s/%d";
    private static final String ORGANIZATION_CATALOGUE_URL = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/organizations/"; // TODO

    @Override
    public String regnskapsregisteretUrl() {
        return REGNSKAPSREGISTERET_URL;
    }

    @Override
    public String organizationCatalogueUrl() {
        return ORGANIZATION_CATALOGUE_URL;
    }
}
