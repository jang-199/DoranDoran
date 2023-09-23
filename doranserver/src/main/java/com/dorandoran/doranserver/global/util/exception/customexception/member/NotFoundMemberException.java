package com.dorandoran.doranserver.global.util.exception.customexception.member;

public class NotFoundMemberException extends RuntimeException{
    public NotFoundMemberException(String message) {
        super(message);
    }
}
