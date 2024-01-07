package com.dorandoran.doranserver.global.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;


@Slf4j
@Configuration
public class FirebaseConfig {
    @Bean
    public FirebaseApp aosFirebaseApp() throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(
                            GoogleCredentials
                                    .fromStream(new ClassPathResource("doran-cb020-firebase-adminsdk-x0ohi-bac3a1516c.json").getInputStream()))
                    .setProjectId("doran-cb020")
                    .build();

        return FirebaseApp.initializeApp(options, "aosApp");
    }

    @Bean
    public FirebaseApp iosFirebaseApp() throws IOException{
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(
                        GoogleCredentials
                                .fromStream(new ClassPathResource("doran-cb020-firebase-adminsdk-x0ohi-dd41248a56.json").getInputStream()))
                .setProjectId("doran-cb020")
                .build();
        return FirebaseApp.initializeApp(options, "iosApp");
    }
}


