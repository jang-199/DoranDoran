package com.dorandoran.doranserver.service;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

@Service
public class FirebaseServiceImpl implements FirebaseService {

    @Override
    public String sendTest(String firebaseToken) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600 * 1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setRestrictedPackageName("com.example.dorandoran") // 애플리케이션 패키지 이름
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("도란도란 테스트")
                                .setBody("테스트 성공!")
                                .build())
                        .build())
                .setToken(firebaseToken) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();

            // Send a message to the device corresponding to the provided registration token.
        String response = FirebaseMessaging.getInstance().send(message);

        return response;
    }
}
