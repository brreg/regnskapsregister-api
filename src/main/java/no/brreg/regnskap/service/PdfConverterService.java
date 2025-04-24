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
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
public class PdfConverterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfConverterService.class);

    private final AarsregnskapCopyProperties aarsregnskapCopyProperties;

    private final ExecutorService executorService;

    public PdfConverterService(AarsregnskapCopyProperties aarsregnskapCopyProperties, ExecutorService executorService) {
        this.aarsregnskapCopyProperties = aarsregnskapCopyProperties;
        this.executorService = executorService;
    }

    public byte[] tiffToPdf(String filename) {
        if (aarsregnskapCopyProperties.experimentalConverter()) {
            return tiffToPdf2(filename);
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

    public byte[] tiffToPdf2(String filename) {
        var tiffFile = new File(filename);
        List<Future<BufferedImage>> imageFutures = new ArrayList<>();
        try {
            int numPages = getNumPages(tiffFile);
            for (int i = 0; i < numPages; i++) {
                int pageIndex = i;
                imageFutures.add(executorService.submit(() -> {
                    try (
                            var fis = new FileInputStream(tiffFile);
                            var imageInputStream = ImageIO.createImageInputStream(fis)
                    ) {
                        var readers = ImageIO.getImageReaders(imageInputStream);
                        if (!readers.hasNext()) {
                            throw new IOException("No reader found for TIFF-image.");
                        }
                        var reader = readers.next();
                        reader.setInput(imageInputStream);

                        var tiff = reader.read(pageIndex);
                        return resizeAndConvertToPng(tiff, 1f);
                    } catch (IOException e) {
                        LOGGER.error("Error reading page " + pageIndex + ": " + e.getMessage());
                        return null;
                    }
                }));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }

        List<BufferedImage> images = new ArrayList<>();
        for (Future<BufferedImage> future : imageFutures) {
            try {
                BufferedImage image = future.get();
                if (image != null) {
                    images.add(image);
                }
            } catch (Exception e) {
                LOGGER.error("Error converting image: " + e.getMessage());
            }
        }

        return generatePdfFromImages(images);
    }

    private int getNumPages(File tiffFile) throws IOException {
        try (
                var fis = new FileInputStream(tiffFile);
                var imageInputStream = ImageIO.createImageInputStream(fis)
        ) {
            var readers = ImageIO.getImageReaders(imageInputStream);
            if (!readers.hasNext()) {
                throw new IOException("No reader found for TIFF-image.");
            }
            var reader = readers.next();
            reader.setInput(imageInputStream);
            return reader.getNumImages(true);
        }
    }

    private BufferedImage resizeAndConvertToPng(BufferedImage image, float scale) {
        int newWidth = (int) (image.getWidth() * scale);
        int newHeight = (int) (image.getHeight() * scale);

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        var scaleTransform = AffineTransform.getScaleInstance((double) newWidth / image.getWidth(), (double) newHeight / image.getHeight());
        var bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
        bilinearScaleOp.filter(image, resizedImage);

        try (var baos = new ByteArrayOutputStream()) {
            ImageIO.write(resizedImage, "PNG", baos);
            byte[] pngBytes = baos.toByteArray();

            try (var bais = new ByteArrayInputStream(pngBytes)) {
                return ImageIO.read(bais);
            }
        } catch (IOException e) {
            LOGGER.error("Error converting image to PNG: " + e.getMessage());
            return null;
        }
    }

    private byte[] generatePdfFromImages(List<BufferedImage> images) {
        try (var os = new ByteArrayOutputStream(); var document = new PDDocument()) {
            for (var image : images) {
                var pageRect = new PDRectangle(image.getWidth(), image.getHeight());
                var page = new PDPage(pageRect);
                document.addPage(page);

                var pdImage = PDImageXObject.createFromByteArray(document, imageToByteArray(image), "image");
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.drawImage(pdImage, 0f, 0f);
                }
            }
            document.save(os);
            return os.toByteArray();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private byte[] imageToByteArray(BufferedImage image) throws IOException {
        try (var baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            return baos.toByteArray();
        }
    }
}
