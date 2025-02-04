package com.desktop.html.parser;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class HTMLParser {

    private final ImageParser imageParser;

    @Autowired
    public HTMLParser(ImageParser imageParser) {
        this.imageParser = imageParser;
    }

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

            String firstText = htmlParse(textArray[0]) + " Ответь коротко.";
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

    @PostMapping("/img-text")
    public String showImgText(@ModelAttribute("HTMLWrapper") HTMLWrapper htmlWrapper, Model model) {

        try {
            String imageUrl = extractImageUrl(htmlWrapper.getHtmlText());

            String fileName = downloadImage(imageUrl);

            String extractedText = googleRecognizeText(fileName);

            Files.deleteIfExists(Paths.get(fileName));

            model.addAttribute("firstText", extractedText);
            model.addAttribute("HTMLWrapper", new HTMLWrapper());
            return "html-parser";
        } catch (Exception e) {
            model.addAttribute("firstText", "Ошибка обработки: " + e.getMessage());
            model.addAttribute("HTMLWrapper", new HTMLWrapper());
            return "html-parser";
        }
    }

    private String extractImageUrl(String html) {
        Document doc = Jsoup.parse(html);
        Element imgElement = doc.select("img").first();
        return (imgElement != null) ? imgElement.attr("src") : html;
    }

    private String downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Проверяем Content-Type
            String contentType = connection.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return null; // Это не изображение
            }

            String fileExtension = contentType.split("/")[1];
            if ("jpeg".equalsIgnoreCase(fileExtension)) {
                fileExtension = "jpg";
            }

            // Создаём временный файл
            String fileName = "src/main/resources/static/tmp/temp_image." + fileExtension;
            File outputFile = new File(fileName);

            // Скачиваем файл
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

    private String recognizeText(String imagePath) throws TesseractException {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/static/tesseract");
        tesseract.setLanguage("eng+rus");
        return tesseract.doOCR(new File(imagePath));
    }

    private String googleRecognizeText(String imagePath) throws IOException {
        Resource credentialsPath = new PathResource("src/main/resources/static/keys/imgparser-449912-03406e2746de.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsPath.getInputStream());
        try (ImageAnnotatorClient visionClient = ImageAnnotatorClient.create(
                ImageAnnotatorSettings.newBuilder()
                        .setCredentialsProvider(() -> credentials)
                        .build()
        )) {
            ByteString imgBytes = ByteString.readFrom(new FileInputStream(imagePath));
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            BatchAnnotateImagesResponse response = visionClient.batchAnnotateImages(
                    java.util.Collections.singletonList(request)
            );
            return response.getResponses(0).getTextAnnotationsList().get(0).getDescription();
        }
    }
}
