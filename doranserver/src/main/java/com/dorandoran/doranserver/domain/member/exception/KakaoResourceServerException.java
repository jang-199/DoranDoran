package com.dorandoran.doranserver.domain.member.exception;

public class KakaoResourceServerException extends Exception{
    public KakaoResourceServerException(String message) {
        super(message);
    }

    public KakaoResourceServerException(Throwable cause) {
        super(cause);
    }
}
