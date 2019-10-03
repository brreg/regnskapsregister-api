package no.regnskap.configuration

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

interface ProfileConditionalValues {
    fun regnskapsregisteretUrl(): String
    fun organizationCatalogueUrl(): String
}

@Service
@Profile("test")
class TestValues : ProfileConditionalValues {
    private val REGNSKAPSREGISTERET_URL = "http://invalid.org/regnskap/"
    private val ORGANIZATION_CATALOGUE_URL = "https://invalid.org/organizations/"

    override fun regnskapsregisteretUrl(): String {
        return REGNSKAPSREGISTERET_URL
    }

    override fun organizationCatalogueUrl(): String {
        return ORGANIZATION_CATALOGUE_URL
    }
}

@Service
@Profile("ut1", "default")
class Ut1Values : ProfileConditionalValues {
    private val REGNSKAPSREGISTERET_URL = "http://rreg.ut1.rreg-dev.brreg.no/regnskap/"
    private val ORGANIZATION_CATALOGUE_URL = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/organizations/"

    override fun regnskapsregisteretUrl(): String {
        return REGNSKAPSREGISTERET_URL
    }

    override fun organizationCatalogueUrl(): String {
        return ORGANIZATION_CATALOGUE_URL
    }
}

@Service
@Profile("prod")
class ProdValues : ProfileConditionalValues {
    private val REGNSKAPSREGISTERET_URL = "http://rreg.ut1.rreg-dev.brreg.no/regnskap/" // TODO
    private val ORGANIZATION_CATALOGUE_URL = "https://publishers-api.ut1.fellesdatakatalog.brreg.no/organizations/" // TODO

    override fun regnskapsregisteretUrl(): String {
        return REGNSKAPSREGISTERET_URL
    }

    override fun organizationCatalogueUrl(): String {
        return ORGANIZATION_CATALOGUE_URL
    }
}