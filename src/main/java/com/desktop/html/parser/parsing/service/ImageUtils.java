package com.desktop.html.parser.parsing.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ImageUtils {

    private static final String DOMAIN_BACKGROUND_IMAGE = "https://kakdela.hh.ru";
    private static final String URL_IMAGE_PATTERN = "background-image:\\s*url\\((?:&quot;|\"|')?(.*?)(?:&quot;|\"|')?\\)";

    public String extractImageUrl(String html) {
        String urlCandidate;

        boolean hasHtmlTags = Pattern.compile("<[^>]+>").matcher(html).find();

        if (hasHtmlTags) {
            Document doc = Jsoup.parse(html);
            Element imgElement = doc.select("img").first();
            if (imgElement != null) {
                urlCandidate = imgElement.attr("src");
            } else {
                urlCandidate = extractBackgroundImageUrl(doc);
                if (urlCandidate == null) return null;
            }
        } else {
            urlCandidate = html;
        }

        if (!urlCandidate.startsWith("http:") && !urlCandidate.startsWith("https:")) {
            urlCandidate = "https:" + urlCandidate;
        }

        try {
            new URL(urlCandidate).toURI();
            return urlCandidate;
        } catch (URISyntaxException | MalformedURLException e) {
            log.warn("Не url и не xml: {}\n Error message: {}", html, e.getMessage());
        }
        return null;
    }

    private String extractBackgroundImageUrl(Document doc) {
        Elements elementsWithStyle = doc.select("[style]");

        Pattern pattern = Pattern.compile(URL_IMAGE_PATTERN);

        for (Element element : elementsWithStyle) {
            String style = element.attr("style");
            Matcher matcher = pattern.matcher(style);

            if (matcher.find()) {
                return DOMAIN_BACKGROUND_IMAGE + matcher.group(1);
            }
        }
        return null;
    }

    public String downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            String contentType = connection.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return null;
            }

            String fileExtension = contentType.split("/")[1];
            if ("jpeg".equalsIgnoreCase(fileExtension)) {
                fileExtension = "jpg";
            }

            String fileName = "src/main/resources/static/tmp/temp_image." + fileExtension;
            File outputFile = new File(fileName);

            try (InputStream in = connection.getInputStream();
                 OutputStream out = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return fileName;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
