package no.brreg.regnskap.jena;


public class ExternalUrls {

    private final String self;
    private final String organizationCatalogue;


    public ExternalUrls(final String self, final String organizationCatalogue) {
        this.self = self;
        this.organizationCatalogue = organizationCatalogue;
    }

    public String getSelf() {
        return self;
    }

    public String createResourceUri(final String orgno, final Integer id) {
        return String.format(getSelf(), orgno, id);
    }

    public String getOrganizationCatalogue() {
        return organizationCatalogue;
    }

}
