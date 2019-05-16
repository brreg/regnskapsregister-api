package no.regnskap.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import no.regnskap.repository.RegnskapRepository;
import no.regnskap.service.xml.ListeRegnskapXml;
import no.regnskap.service.xml.RegnskapXml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.SQLException;

@Service
public class UpdateService {

    public static final String TRUE_STRING = "J";

    @Autowired
    private RegnskapRepository mongoRepository;

    public ListeRegnskapXml update() throws IOException, SQLException {
        ListeRegnskapXml latestRegnskap = getXmlData();
        for (RegnskapXml regnskapXml : latestRegnskap.getRegnskapsListe()) {
            mongoRepository.save(regnskapXml);
        }
        return latestRegnskap;
    }

    private ListeRegnskapXml getXmlData() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(getXmlFile(), ListeRegnskapXml.class);
    }

    private String getXmlFile() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("specification/examples/20190327213825-masse-2.xml");
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
