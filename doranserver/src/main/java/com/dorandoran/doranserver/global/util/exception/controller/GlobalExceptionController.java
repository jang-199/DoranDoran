package com.dorandoran.doranserver.global.util.exception.controller;

import com.dorandoran.doranserver.global.util.domain.ErrorReport;
import com.dorandoran.doranserver.global.util.exception.customexception.comment.NotFoundCommentException;
import com.dorandoran.doranserver.global.util.exception.customexception.member.NotFoundMemberException;
import com.dorandoran.doranserver.global.util.exception.customexception.notification.NotFoundNotificationHistoryException;
import com.dorandoran.doranserver.global.util.exception.customexception.post.NotFoundPostException;
import com.dorandoran.doranserver.global.util.exception.customexception.reply.NotFoundReplyException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.dorandoran.doranserver.domain")
public class GlobalExceptionController {

    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<ObjectNode> notFoundMemberException(Exception e){
        ObjectNode errorReport = new ErrorReport().makeErrorReport("notFoundMemberException", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorReport);
    }

    @ExceptionHandler(NotFoundPostException.class)
    public ResponseEntity<ObjectNode> notFoundPostException(Exception e){
        ObjectNode errorReport = new ErrorReport().makeErrorReport("notFoundPostException", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorReport);
    }

    @ExceptionHandler(NotFoundCommentException.class)
    public ResponseEntity<ObjectNode> notFoundCommentException(Exception e){
        ObjectNode errorReport = new ErrorReport().makeErrorReport("notFoundCommentException", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorReport);
    }

    @ExceptionHandler(NotFoundReplyException.class)
    public ResponseEntity<ObjectNode> notFoundReplyException(Exception e){
        ObjectNode errorReport = new ErrorReport().makeErrorReport("notFoundReplyException", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorReport);
    }

    @ExceptionHandler(NotFoundNotificationHistoryException.class)
    public ResponseEntity<ObjectNode> notFoundNotificationHistoryException(Exception e){
        ObjectNode errorReport = new ErrorReport().makeErrorReport("notFoundNotificationHistoryException", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorReport);
    }
}
