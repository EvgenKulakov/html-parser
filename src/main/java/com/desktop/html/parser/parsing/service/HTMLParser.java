package com.desktop.html.parser.parsing.service;

import com.desktop.html.parser.dto.TextResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class HTMLParser {

    private final ImgParser googleParser;
    private final ImgParser tesseractParser;
    private final ImageUtils imageUtils;

    @Autowired
    public HTMLParser(@Qualifier("googleParser") ImgParser googleParser,
                      @Qualifier("tesseractParser") ImgParser tesseractParser,
                      ImageUtils imageUtils) {
        this.googleParser = googleParser;
        this.tesseractParser = tesseractParser;
        this.imageUtils = imageUtils;
    }

    public TextResponse parse(String htmlText, ParserType parserType) {
        TextResponse textResponse = new TextResponse();

        String imageUrl = imageUtils.extractImageUrl(htmlText);
        if (imageUrl != null) {
            try {
                String fileName = imageUtils.downloadImage(imageUrl);
                if (fileName != null) {
                    String imgText = switch (parserType) {
                        case TESSERACT -> tesseractParser.imgParse(fileName);
                        case GOOGLE -> googleParser.imgParse(fileName);
                    };
                    Files.deleteIfExists(Path.of(fileName));
                    textResponse.setImgText(imgText);
                } else {
                    textResponse.setImgText("Это не изображение: " + imageUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
                textResponse.setImgText("Ошибка парсера: " + e.getMessage());
            }
        }

        if (htmlText.contains("Обязательный ответ")) {
            String[] textArray = htmlText.split("Обязательный ответ");

            String firstText = htmlParse(textArray[0]) + " Ответь коротко.";
            List<String> secondText = spanParse(textArray[1]);

            textResponse.setFirstText(firstText);
            textResponse.setSecondText(secondText);
        } else {
            String textFromHtml = htmlParse(htmlText);
            textResponse.setFirstText(textFromHtml);
        }

        return textResponse;
    }

    private String htmlParse(String htmlText) {
        return Jsoup.parse(htmlText).text();
    }

    private List<String> spanParse(String htmlText) {
        return Jsoup.parse(htmlText).select("span").stream().map(Element::text).toList();
    }
}
