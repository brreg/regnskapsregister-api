package no.brreg.regnskap.jena;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;


public class SCHEMA {

    public static String getURI() {
        return "http://schema.org/";
    }

    private static final Model model = ModelFactory.createDefaultModel();

    public static final Property currency = model.createProperty(getURI(), "currency");
    public static final Property startDate = model.createProperty(getURI(), "startDate");
    public static final Property endDate = model.createProperty(getURI(), "endDate");

}