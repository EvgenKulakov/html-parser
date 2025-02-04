package com.desktop.html.parser.parsing.service;

import net.sourceforge.tess4j.ITesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class TesseractParser implements ImgParser {

    private final ITesseract tesseract;

    @Autowired
    public TesseractParser(ITesseract tesseract) {
        this.tesseract = tesseract;
    }

    @Override
    public String imgParse(String imagePath) throws Exception {
        return tesseract.doOCR(new File(imagePath));
    }
}
