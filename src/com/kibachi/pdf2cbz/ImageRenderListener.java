package com.kibachi.pdf2cbz;

import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

/**
 * Created with IntelliJ IDEA.
 * User: kengelke
 * Date: 01.05.13
 * Time: 20:52
 */
public class ImageRenderListener implements RenderListener {

    private byte[] image;
    private String fileType;

    public void beginTextBlock() {
    }

    public void endTextBlock() {
    }

    public void renderText(TextRenderInfo tri) {
    }

    public void renderImage(ImageRenderInfo iri) {
        try {
            image = iri.getImage().getImageAsBytes();
            fileType = iri.getImage().getFileType();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public byte[] getImage() {
        return image;
    }

    public String getFileType() {
        return fileType;
    }
}