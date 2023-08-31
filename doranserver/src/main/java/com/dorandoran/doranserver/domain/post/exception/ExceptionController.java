package com.dorandoran.doranserver.domain.post.exception;

import com.dorandoran.doranserver.domain.post.controller.PostController;
import com.dorandoran.doranserver.domain.post.controller.RetrieveClosePostController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.LinkedHashMap;

@Slf4j

public class ExceptionController {

    @RestControllerAdvice(assignableTypes = PostController.class)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> fileUploadException(Exception e){
        log.info("파일 업로드 크기 제한 exception",e);
        return new ResponseEntity<>("파일 업로드 크기 제한", HttpStatus.BAD_REQUEST);
    }

    @RestControllerAdvice(assignableTypes = RetrieveClosePostController.class)
    public static class MissingServletRequestParameterExceptionHandler{
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<?> MissingParameterException(Exception e) {
            LinkedHashMap<String, Object> errorReport = new LinkedHashMap<>();
            errorReport.put("Code", "MissingServletRequestParameterException");
            errorReport.put("Message", e.getMessage());
            return ResponseEntity.badRequest().body(errorReport);
        }
    }
}
