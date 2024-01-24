package com.example.task1.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static org.apache.pdfbox.Loader.*;

@Service
public class TextExtractService {

    public String extractText(final MultipartFile file) throws IOException {
        String text;

        final PDDocument doc = loadPDF(file.getBytes());
        final PDFTextStripper textStripper = new PDFTextStripper();
        text = textStripper.getText(doc);

        return text;
    }

    public String extractText(final File file) throws IOException {
        String text;

        final PDDocument doc = loadPDF(file);
        final PDFTextStripper textStripper = new PDFTextStripper();
        text = textStripper.getText(doc);

        return text;
    }
}
