package com.desktop.html.parser.parsing.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;

@Service
public class GoogleParser implements ImgParser {

    private final ImageAnnotatorClient imageAnnotatorClient;

    @Autowired
    public GoogleParser(ImageAnnotatorClient imageAnnotatorClient) {
        this.imageAnnotatorClient = imageAnnotatorClient;
    }

    @PreDestroy
    public void closeClient() {
        imageAnnotatorClient.close();
    }

    @Override
    public String imgParse(String imagePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(imagePath)) {
            ByteString imgBytes = ByteString.readFrom(fis);
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            BatchAnnotateImagesResponse response = imageAnnotatorClient.batchAnnotateImages(
                    java.util.Collections.singletonList(request)
            );
            return response.getResponses(0).getTextAnnotationsList().get(0).getDescription();
        }
    }
}
