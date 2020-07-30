package no.regnskap;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JenaResponseReader {

    private static Reader resourceAsReader(final String resourceName) {
        return new InputStreamReader(JenaResponseReader.class.getClassLoader().getResourceAsStream(resourceName), StandardCharsets.UTF_8);
    }

    private static String resourceAsString(final String resourceName) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader bufferedReader = new BufferedReader(JenaResponseReader.resourceAsReader(resourceName))) {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    public static Model getExpectedResponse(String filename, Map<String,String> patches, String lang) throws IOException {
        Model expected = ModelFactory.createDefaultModel();
        String fileContent = JenaResponseReader.resourceAsString(filename);
        for (Map.Entry<String,String> patch : patches.entrySet()) {
            fileContent = fileContent.replace(patch.getKey(), patch.getValue());
        }
        expected.read(new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)), "", lang);
        return expected;
    }

    public static Model parseResponse(String response, String lang) {
        Model responseModel = ModelFactory.createDefaultModel();
        responseModel.read(new StringReader(response), "", lang);
        return responseModel;
    }
}
