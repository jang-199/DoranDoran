package com.dorandoran.doranserver.global.util;

public class MemberMatcherUtil {
    public static Boolean compareEmails(String objectEmail, String userEmail){
        //todo util로 패키지 변경
        return objectEmail.equals(userEmail) ? Boolean.TRUE : Boolean.FALSE;
    }
}
