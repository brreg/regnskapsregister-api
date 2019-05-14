package no.regnskap.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import no.regnskap.service.xml.ListeRegnskapXml;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class UpdateService {

    public ListeRegnskapXml getXmlData() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(getXmlFile(), ListeRegnskapXml.class);
    }

    private String getXmlFile() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("20190327213825-masse-2.xml");
        return readFromInputStream(inputStream);
    }

    private String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
}
