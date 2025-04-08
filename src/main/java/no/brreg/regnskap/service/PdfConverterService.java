package no.brreg.regnskap.service;

import no.brreg.regnskap.controller.exception.InternalServerError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.*;

@Service
public class PdfConverterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfConverterService.class);

    public byte[] tiffToPdf(String filename) {
        var tiffFile = new File(filename);

        try (
                var outputStream = new ByteArrayOutputStream();
                var errorOutputStream = new ByteArrayOutputStream()
        ) {

            String[] command = {
                    "gm",
                    "convert",
                    tiffFile.getAbsolutePath(),
                    "pdf:-"
            };

            var processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            var process = processBuilder.start();
            try (var inputStream = process.getInputStream()) {
                inputStream.transferTo(outputStream);
            }

            try (var errorStream = process.getErrorStream()) {
                errorStream.transferTo(errorOutputStream);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                String errorMessage = errorOutputStream.toString();
                throw new IOException("Failed to convert %s to PDF: %s".formatted(filename, errorMessage));
            }

            return outputStream.toByteArray();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerError(e);
        }
    }
}
