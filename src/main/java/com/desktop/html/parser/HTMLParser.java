package com.desktop.html.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class HTMLParser {

    @GetMapping("/")
    public String showQueryWindow(Model model) {
        model.addAttribute("HTMLWrapper", new HTMLWrapper());
        model.addAttribute("firstText", "");
        return "html-parser";
    }

    @PostMapping("/parsing")
    public String showResult(@ModelAttribute("HTMLWrapper") HTMLWrapper htmlWrapper, Model model) {
        String htmlText = htmlWrapper.getHtmlText();

        if (htmlText.contains("Обязательный ответ")) {
            String[] textArray = htmlText.split("Обязательный ответ");

            String firstText = htmlParse(textArray[0]);
            List<String> secondText = spanParse(textArray[1]);

            model.addAttribute("firstText", firstText);
            model.addAttribute("secondText", secondText);

        } else {
            String textFromHtml = htmlParse(htmlWrapper.getHtmlText());
            model.addAttribute("firstText", textFromHtml);
        }

        model.addAttribute("HTMLWrapper", new HTMLWrapper());
        return "html-parser";
    }

    private String htmlParse(String htmlText) {
        return Jsoup.parse(htmlText).text();
    }

    private List<String> spanParse(String htmlText) {
        return Jsoup.parse(htmlText).select("span").stream().map(Element::text).toList();
    }
}
