package no.regnskap.service;

import no.regnskap.model.Checksum;
import no.regnskap.model.RegnskapDB;
import no.regnskap.repository.ChecksumRepository;
import no.regnskap.repository.RegnskapRepository;
import no.regnskap.model.RegnskapXmlWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.util.List;

import static no.regnskap.mapper.RegnskapMapperKt.mapXmlListForPersistence;
import static no.regnskap.mapper.RegnskapXmlMapperKt.deserializeXmlString;

@Service
public class UpdateService {


    @Autowired
    private RegnskapRepository regnskapRepository;

    @Autowired
    private ChecksumRepository checksumRepository;

    public RegnskapXmlWrap update() throws IOException {
        String xmlString = getXmlString();
        String checksum = DigestUtils.md5DigestAsHex(xmlString.getBytes());

        if(checksumRepository.findOneByChecksum(checksum) == null) {
            RegnskapXmlWrap deserialized = deserializeXmlString(xmlString);
            List<RegnskapDB> listToPersist = mapXmlListForPersistence(deserialized.getList());

            regnskapRepository.saveAll(listToPersist);

            checksumRepository.save(new Checksum(checksum));

            return deserialized;
        }

        return null;
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
