package no.brreg.regnskap.service;

import kotlin.ranges.IntRange;
import no.brreg.regnskap.controller.exception.InternalServerError;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.*;

@Service
public class PdfConverterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfConverterService.class);

    public byte[] tiffToPdf(InputStream inputStream) {
        try (
                var os = new ByteArrayOutputStream();
                var document = new PDDocument();
                var imageInputStream = ImageIO.createImageInputStream(inputStream)
        ) {
            var readers = ImageIO.getImageReadersByFormatName("tiff");
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

                var pdImage = LosslessFactory.createFromImage(document, tiff);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.drawImage(pdImage, 0f, 0f, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
                }
            }
            document.save(os);
            return os.toByteArray();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerError(e);
        }
    }
}
