package com.dorandoran.doranserver.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

//@Slf4j
//@Component
//public class FirebaseConfig {
//    @PostConstruct
//    public void initialize() throws IOException {
//        FirebaseOptions options = FirebaseOptions.builder()
//                .setCredentials(GoogleCredentials.fromStream(new ClassPathResource("dorandoran-374312-firebase-adminsdk-1efcc-cd7ff6a321.json").getInputStream()))
//                .setProjectId("dorandoran-374312")
//                .build();
//        if (FirebaseApp.getApps().isEmpty()) {
//            FirebaseApp.initializeApp(options);
//        }
//    }
//}