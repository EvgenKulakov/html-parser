package com.desktop.html.parser.controller;

import com.desktop.html.parser.dto.HTMLWrapper;
import com.desktop.html.parser.dto.TextResponse;
import com.desktop.html.parser.parsing.service.HTMLParser;
import com.desktop.html.parser.parsing.service.ParserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HTMLParseController {

    private final HTMLParser htmlParser;

    @Autowired
    public HTMLParseController(HTMLParser htmlParser) {
        this.htmlParser = htmlParser;
    }

    @GetMapping(value = {"/", "/parsing", "/parsing-with-tesseract", "/parsing-with-google"})
    public String showQueryWindow(Model model) {
        model.addAttribute("HTMLWrapper", new HTMLWrapper());
        model.addAttribute("firstText", "");
        return "html-parser";
    }

    @PostMapping("/parsing")
    public String showStandardParse(@ModelAttribute("HTMLWrapper") HTMLWrapper htmlWrapper, Model model) {
        String htmlText = htmlWrapper.getHtmlText();

        TextResponse textResponse = htmlParser.nativeParse(htmlText);

        model.addAttribute("imgText", textResponse.getImgText());
        model.addAttribute("firstText", textResponse.getFirstText());
        model.addAttribute("secondText", textResponse.getSecondText());
        model.addAttribute("HTMLWrapper", new HTMLWrapper());
        return "html-parser";
    }

    @PostMapping("/parsing-with-tesseract")
    public String showTesseractParse(@ModelAttribute("HTMLWrapper") HTMLWrapper htmlWrapper, Model model) {
        String htmlText = htmlWrapper.getHtmlText();

        TextResponse textResponse = htmlParser.parse(htmlText, ParserType.TESSERACT);

        model.addAttribute("imgText", textResponse.getImgText());
        model.addAttribute("firstText", textResponse.getFirstText());
        model.addAttribute("secondText", textResponse.getSecondText());
        model.addAttribute("HTMLWrapper", new HTMLWrapper());
        return "html-parser";
    }

    @PostMapping("/parsing-with-google")
    public String showGoogleParse(@ModelAttribute("HTMLWrapper") HTMLWrapper htmlWrapper, Model model) {
        String htmlText = htmlWrapper.getHtmlText();

        TextResponse textResponse = htmlParser.parse(htmlText, ParserType.GOOGLE);

        model.addAttribute("imgText", textResponse.getImgText());
        model.addAttribute("firstText", textResponse.getFirstText());
        model.addAttribute("secondText", textResponse.getSecondText());
        model.addAttribute("HTMLWrapper", new HTMLWrapper());
        return "html-parser";
    }
}
