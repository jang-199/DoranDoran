package com.dorandoran.doranserver.exception;

public class CannotFindReplyException extends RuntimeException {
    public CannotFindReplyException() {
        super();
    }

    public CannotFindReplyException(String message) {
        super(message);
    }

    public CannotFindReplyException(String message, Throwable cause) {
        super(message, cause);

    }

    public CannotFindReplyException(Throwable cause) {
        super(cause);
    }
}
