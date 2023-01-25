package com.dorandoran.doranserver.service;

import com.google.firebase.messaging.FirebaseMessagingException;

public interface FirebaseService {
    public String sendTest(String firebaseToken) throws FirebaseMessagingException;
}
