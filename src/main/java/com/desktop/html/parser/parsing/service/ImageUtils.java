package com.desktop.html.parser.parsing.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ImageUtils {

    public String extractImageUrl(String html) {
        String urlCandidate;

        boolean hasHtmlTags = Pattern.compile("<[^>]+>").matcher(html).find();

        if (hasHtmlTags) {
            Document doc = Jsoup.parse(html);
            Element imgElement = doc.select("img").first();
            if (imgElement != null) {
                urlCandidate = imgElement.attr("src");
            } else {
                return null;
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
            log.warn("{} - не url и не xml: {}", html, e.getMessage());
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
