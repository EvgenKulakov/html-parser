package com.desktop.html.parser.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class GoogleConfiguration {

    @Value("${google.cloud.credentials.path}")
    private String credentialsPath;

    @Bean
    GoogleCredentials googleCredentials() {
        Resource resource = new PathResource(credentialsPath);
        try {
            return GoogleCredentials.fromStream(resource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    ImageAnnotatorClient imageAnnotatorClient() {
        try {
            return ImageAnnotatorClient.create(
                    ImageAnnotatorSettings.newBuilder()
                            .setCredentialsProvider(this::googleCredentials)
                            .build()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
