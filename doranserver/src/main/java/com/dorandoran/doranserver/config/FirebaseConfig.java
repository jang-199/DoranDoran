package com.dorandoran.doranserver.config;

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
/*    @PostConstruct
    public void initialize() throws IOException {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials
                            .fromStream(new ClassPathResource("dorandoran-374312-firebase-adminsdk-1efcc-88b663b5a1.json").getInputStream()))
                    .setProjectId("dorandoran-374312")
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        }catch (IOException e){
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }*/

    @Bean
    public FirebaseApp aosFirebaseApp() throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(
                        GoogleCredentials
                                .fromStream(new ClassPathResource("dorandoran-374312-firebase-adminsdk-1efcc-88b663b5a1.json").getInputStream()))
                .setProjectId("dorandoran-374312")
                .build();
        return FirebaseApp.initializeApp(options, "aosApp");
    }

    @Bean
    public FirebaseApp iosFirebaseApp() throws IOException{
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(
                        GoogleCredentials
                                .fromStream(new ClassPathResource("dorandoran-374312-firebase-adminsdk-1efcc-31bcc99fe9.json").getInputStream()))
                .setProjectId("dorandoran-374312")
                .build();
        return FirebaseApp.initializeApp(options, "iosApp");
    }
}


