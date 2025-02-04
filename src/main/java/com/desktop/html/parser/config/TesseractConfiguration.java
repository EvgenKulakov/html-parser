package com.desktop.html.parser.config;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TesseractConfiguration {

    @Value("${tesseract.data.path}")
    private String tesseractDataPath;

    @Bean
    public ITesseract iTesseract() {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath(tesseractDataPath);
        tesseract.setLanguage("eng+rus");
        return tesseract;
    }
}
