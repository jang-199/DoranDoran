package com.dorandoran.doranserver.service;

import org.springframework.stereotype.Service;

@Service
public class CommonService {
    public Boolean compareEmails(String objectEmail, String userEmail){
        return objectEmail.equals(userEmail) ? Boolean.TRUE : Boolean.FALSE;
    }
}