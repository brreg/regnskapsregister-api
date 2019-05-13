package no.regnskap.service;

import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class UpdateService {

    public String getXmlFile() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("20190327213825-masse-2.xml");
            return readFromInputStream(inputStream);
        } catch (IOException e) {
            return e.getMessage();
        }
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
