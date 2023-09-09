package com.dorandoran.doranserver.domain.post.exception;

import com.dorandoran.doranserver.domain.post.controller.PostController;
import com.dorandoran.doranserver.domain.post.controller.RetrieveClosePostController;
import com.dorandoran.doranserver.domain.post.service.PostService;
import com.dorandoran.doranserver.domain.post.service.PostServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.LinkedHashMap;

@Slf4j
public class ExceptionController {

    @RestControllerAdvice(assignableTypes = PostController.class)
    public static class MaxUploadSizeExceededExceptionHandler {
        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<?> fileUploadException(Exception e) {
            return new ResponseEntity<>("파일 업로드 크기 제한", HttpStatus.BAD_REQUEST);
        }
    }

    @RestControllerAdvice(assignableTypes = PostController.class)
    public static class UnsupportedImageExtensionExceptionHandler {
        @ExceptionHandler(UnsupportedImageExtensionException.class)
        public ResponseEntity<?> unSupportedImageExtensionException(UnsupportedImageExtensionException e) {
            LinkedHashMap<String, String> errorRepost = new LinkedHashMap<>();
            errorRepost.put("Error", "UnsupportedImageExtensionException");
            errorRepost.put("Message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorRepost);
        }
    }

    @RestControllerAdvice(assignableTypes = RetrieveClosePostController.class)
    public static class MissingServletRequestParameterExceptionHandler{
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<?> MissingParameterException(Exception e) {
            LinkedHashMap<String, Object> errorReport = new LinkedHashMap<>();
            errorReport.put("Error", "MissingServletRequestParameterException");
            errorReport.put("Message", e.getMessage());
            return ResponseEntity.badRequest().body(errorReport);
        }
    }
}
