package no.regnskap.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import no.regnskap.mapper.RegnskapMapper;
import no.regnskap.model.persistance.Checksum;
import no.regnskap.model.persistance.RegnskapDB;
import no.regnskap.repository.ChecksumRepository;
import no.regnskap.repository.RegnskapRepository;
import no.regnskap.model.xml.RegnskapWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.util.List;

@Service
public class UpdateService {

    @Autowired
    private RegnskapRepository regnskapRepository;

    @Autowired
    private ChecksumRepository checksumRepository;

    public RegnskapWrap update() throws IOException {
        String xmlString = getXmlString();
        String checksum = DigestUtils.md5DigestAsHex(xmlString.getBytes());

        if(checksumRepository.findOneByChecksum(checksum) == null) {
            RegnskapWrap deserialized = deserializeXmlString(xmlString);
            List<RegnskapDB> listToPersist = RegnskapMapper.mapFromXmlForPersistance(deserialized.getList());

            regnskapRepository.saveAll(listToPersist);

            checksumRepository.save(new Checksum(checksum));

            return deserialized;
        }

        return null;
    }

    private RegnskapWrap deserializeXmlString(String xmlString) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(xmlString, RegnskapWrap.class);
    }

    private String getXmlString() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream xmlInputStream = classLoader.getResourceAsStream("specification/examples/20190327213825-masse-2.xml");
        return readFromInputStream(xmlInputStream);
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
