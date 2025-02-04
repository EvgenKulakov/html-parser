package com.desktop.html.parser;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
//@Slf4j
public class ImageParser {

    public String parse() {
        File imageFile = new File("C:/Users/ekulakov/IdeaProjects/html-parser/src/main/resources/static/image1.png"); // Путь к изображению
        ITesseract instance = new Tesseract();
        instance.setLanguage("eng+rus");

        String result = null;
        try {
            result = instance.doOCR(imageFile);
        } catch (TesseractException e) {
//            log.error(e.getMessage());
        }
        return result;
    }
}
