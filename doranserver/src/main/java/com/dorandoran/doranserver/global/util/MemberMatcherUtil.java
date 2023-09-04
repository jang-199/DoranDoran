package com.dorandoran.doranserver.global.util;

public class MemberMatcherUtil {
    public static Boolean compareEmails(String objectEmail, String userEmail){
        return objectEmail.equals(userEmail) ? Boolean.TRUE : Boolean.FALSE;
    }
}
