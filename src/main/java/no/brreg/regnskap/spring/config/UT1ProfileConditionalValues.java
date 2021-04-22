package no.brreg.regnskap.spring.config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Service
@Profile({"ut1", "default"})
class UT1ProfileConditionalValues implements ProfileConditionalValues {
    private static final String REGNSKAPSREGISTERET_URL = "https://regnskapsregister-api.apps.ocp-st.regsys.brreg.no/regnskapregisteret/regnskap/%s/%d";
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
