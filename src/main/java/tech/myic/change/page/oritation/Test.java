/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.myic.change.page.oritation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.pdfbox.io.IOUtils;

/**
 *
 * @author jules
 */
public class Test {

    public static void main(String[] args)
            throws FileNotFoundException, IOException {
        File file = new File("/home/jules/Downloads/in2.png");
        if (!file.exists()) {
            throw new FileNotFoundException("File not found");
        }

        String documentName = file.getAbsolutePath();

        ImageType imageType = ImageType.JPEG;
        if (documentName.toLowerCase().endsWith(".png")) {
            imageType = ImageType.PNG;
        }

        ByteBuffer imageBytes;
        try (InputStream in = new FileInputStream(documentName)) {
            imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(in));
        }

        if (imageBytes == null) {
            return;
        }

        BufferedImage image = getImageOrNull(documentName);
        if (image == null) {
            return;
        }

        PDFDocument pdfDocument = new PDFDocument();
        List<TextLine> lines = new LinkedList<>();
        lines.add(new TextLine(0, 0, 0, 0, "Text for testing"));

        pdfDocument.addPage(image, imageType, lines);

        File temFile = new File("/tmp/out.pdf");

        String outputFileName = temFile.getAbsolutePath();

        try (OutputStream outputStream = new FileOutputStream(outputFileName)) {
            pdfDocument.save(outputStream);
            pdfDocument.close();
        }

        System.out.println("Done!");
    }

    private static BufferedImage getImageOrNull(String documentName)
            throws IOException {
        BufferedImage image;

        try (InputStream in = new FileInputStream(documentName)) {
            image = ImageIO.read(in);
        }
        if (image == null) {
            return null;
        }

        return image;
    }
}
