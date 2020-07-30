package no.regnskap.jena;


public class ExternalUrls {

    private String self;
    private String organizationCatalogue;


    public ExternalUrls(final String self, final String organizationCatalogue) {
        this.self = self;
        this.organizationCatalogue = organizationCatalogue;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(final String self) {
        this.self = self;
    }

    public String getOrganizationCatalogue() {
        return organizationCatalogue;
    }

    public void setOrganizationCatalogue(final String organizationCatalogue) {
        this.organizationCatalogue = organizationCatalogue;
    }

}
