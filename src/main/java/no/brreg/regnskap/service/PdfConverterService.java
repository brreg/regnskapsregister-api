package no.brreg.regnskap.service;

import kotlin.ranges.IntRange;
import no.brreg.regnskap.config.properties.AarsregnskapCopyProperties;
import no.brreg.regnskap.controller.exception.InternalServerError;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@Service
public class PdfConverterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfConverterService.class);

    private final AarsregnskapCopyProperties aarsregnskapCopyProperties;

    public PdfConverterService(AarsregnskapCopyProperties aarsregnskapCopyProperties) {
        this.aarsregnskapCopyProperties = aarsregnskapCopyProperties;
    }

    public byte[] tiffToPdf(String filename) {
        if (aarsregnskapCopyProperties.useGraphicsMagick()) {
            return tiffToPdf_gm(filename);
        }


        var tiffFile = new File(filename);

        try (
                var os = new ByteArrayOutputStream();
                var document = new PDDocument();
                var fis = new FileInputStream(tiffFile);
                var imageInputStream = ImageIO.createImageInputStream(fis)
        ) {
            var readers = ImageIO.getImageReaders(imageInputStream);
            if (!readers.hasNext()) {
                throw new IOException("No reader found for TIFF-image.");
            }

            var reader = readers.next();
            reader.setInput(imageInputStream);

            int numPages = reader.getNumImages(true);
            for (var pageIndex : new IntRange(0, numPages - 1)) {
                var tiff = reader.read(pageIndex);

                var pageRect = new PDRectangle(tiff.getWidth(), tiff.getHeight());
                var page = new PDPage(pageRect);
                document.addPage(page);

                var pdImage = PDImageXObject.createFromByteArray(document, imageToByteArray(tiff), "image");

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.drawImage(pdImage, 0f, 0f);
                }
            }
            document.save(os);
            return os.toByteArray();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerError(e);
        }
    }


    public byte[] tiffToPdf_gm(String filename) {
        var tiffFile = new File(filename);

        try (
                var outputStream = new ByteArrayOutputStream();
                var errorOutputStream = new ByteArrayOutputStream()
        ) {

            String[] command = {
                    "gm",
                    "convert",
                    "-limit", "memory", "1GB",
                    "-limit", "map", "1GB",
                    "-limit", "threads", "1",
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


    private byte[] imageToByteArray(BufferedImage image) throws IOException {
        try (var baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "TIFF", baos);
            return baos.toByteArray();
        }
    }
}
