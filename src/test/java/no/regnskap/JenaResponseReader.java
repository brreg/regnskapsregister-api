package no.regnskap;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class JenaResponseReader {

    private Reader resourceAsReader(final String resourceName) {
        return new InputStreamReader(getClass().getClassLoader().getResourceAsStream(resourceName), StandardCharsets.UTF_8);
    }

    public Model getExpectedResponse(String filename, String lang) {
        Model expected = ModelFactory.createDefaultModel();
        expected.read(resourceAsReader(filename), "", lang);
        return expected;
    }

    public Model parseResponse(String response, String lang) {
        Model responseModel = ModelFactory.createDefaultModel();
        responseModel.read(new StringReader(response), "", lang);
        return responseModel;
    }
}
