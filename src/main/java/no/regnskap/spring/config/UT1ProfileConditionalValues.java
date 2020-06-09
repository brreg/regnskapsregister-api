package no.regnskap.spring.config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Service
@Profile({"ut1", "default"})
class UT1ProfileConditionalValues implements ProfileConditionalValues {
    private static final String REGNSKAPSREGISTERET_URL = "http://rreg.ut1.rreg-dev.brreg.no/regnskap/";
    private static final String ORGANIZATION_CATALOGUE_URL = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/organizations/";

    @Override
    public String regnskapsregisteretUrl() {
        return REGNSKAPSREGISTERET_URL;
    }

    @Override
    public String organizationCatalogueUrl() {
        return ORGANIZATION_CATALOGUE_URL;
    }
}
