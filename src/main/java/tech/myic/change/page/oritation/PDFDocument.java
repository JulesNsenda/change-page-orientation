package tech.myic.change.page.oritation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;

public class PDFDocument {

    final PDFont font = PDType1Font.COURIER;
    private final PDDocument document;

    public PDFDocument() {
        this.document = new PDDocument();
    }

    private FontInfo calculateFontSize(String text, float bbWidth, float bbHeight)
            throws IOException {
        int fontSize = 17;
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        float textHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

        if (textWidth > bbWidth) {
            while (textWidth > bbWidth) {
                fontSize -= 1;
                textWidth = font.getStringWidth(text) / 1000 * fontSize;
                textHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
            }
        } else if (textWidth < bbWidth) {
            while (textWidth < bbWidth) {
                fontSize += 1;
                textWidth = font.getStringWidth(text) / 1000 * fontSize;
                textHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
            }
        }

        FontInfo fi = new FontInfo();
        fi.fontSize = fontSize;
        fi.textHeight = textHeight;
        fi.textWidth = textWidth;

        return fi;
    }

    public void addPage(BufferedImage image, ImageType imageType, List<TextLine> lines)
            throws IOException {
        float width = image.getWidth();
        float height = image.getHeight();

        PDRectangle box = new PDRectangle(width, height);

        PDPage page = new PDPage(box);

        PDRectangle mediaBox = page.getMediaBox();
        if ((mediaBox.getWidth() <= mediaBox.getHeight()) && (page.getRotation() == 90 || page.getRotation() == 270)) {
            page.setRotation(0);
        }

        page.setMediaBox(PDRectangle.A4);

        this.document.addPage(page);

        PDImageXObject pdImage;

        if (imageType == ImageType.JPEG) {
            pdImage = JPEGFactory.createFromImage(this.document, image);
        } else {
            pdImage = LosslessFactory.createFromImage(this.document, image);
        }

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.drawImage(pdImage, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());

            contentStream.setRenderingMode(RenderingMode.NEITHER);

            for (TextLine cline : lines) {
                FontInfo fontInfo = calculateFontSize(cline.text, (float) cline.width * width, (float) cline.height * height);
                contentStream.beginText();
                contentStream.setFont(this.font, fontInfo.fontSize);
                contentStream.newLineAtOffset((float) cline.left * width, (float) (height - height * cline.top - fontInfo.textHeight));
                contentStream.showText(cline.text);
                contentStream.endText();
            }
        }
    }

    public void save(String path)
            throws IOException {
        this.document.save(new File(path));
    }

    public void save(OutputStream os)
            throws IOException {
        this.document.save(os);
    }

    public void close()
            throws IOException {
        this.document.close();
    }
}
