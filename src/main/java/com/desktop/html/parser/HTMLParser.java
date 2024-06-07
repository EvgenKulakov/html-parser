package com.desktop.html.parser;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HTMLParser {

    @GetMapping("/")
    public String showQueryWindow(Model model) {
        model.addAttribute("HTMLWrapper", new HTMLWrapper());
        model.addAttribute("resultText", "");
        return "html-parser";
    }

    @PostMapping("/")
    public String showResult(@ModelAttribute("HTMLWrapper") HTMLWrapper htmlWrapper, Model model) {
        String resultText = getResultText(htmlWrapper.getHtmlText());
        model.addAttribute("HTMLWrapper", new HTMLWrapper());
        model.addAttribute("resultText", resultText);
        return "html-parser";
    }

    String getResultText(String htmlText) {
        return Jsoup.parse(htmlText).text();
    }
}
