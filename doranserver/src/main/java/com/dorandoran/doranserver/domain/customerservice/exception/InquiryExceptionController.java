package com.dorandoran.doranserver.domain.customerservice.exception;

import com.dorandoran.doranserver.domain.customerservice.controller.InquiryPostController;
import com.dorandoran.doranserver.global.util.domain.ErrorReport;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = InquiryPostController.class)
public class InquiryExceptionController {

    @ExceptionHandler(NotFoundInquiryPostException.class)
    public ResponseEntity<ObjectNode> notFoundInquiryPostException(Exception e){
        ObjectNode errorReport = new ErrorReport().makeErrorReport("notFoundInquiryPostException", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorReport);
    }
}
