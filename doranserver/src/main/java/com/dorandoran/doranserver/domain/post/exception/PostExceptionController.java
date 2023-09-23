package com.dorandoran.doranserver.domain.post.exception;

import com.dorandoran.doranserver.domain.post.controller.PostController;
import com.dorandoran.doranserver.domain.post.controller.RetrieveClosePostController;
import com.dorandoran.doranserver.global.util.domain.ErrorReport;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
public class PostExceptionController {

    @RestControllerAdvice(assignableTypes = PostController.class)
    public static class MaxUploadSizeExceededExceptionHandler {
        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<?> maxUploadSizeExceededException(Exception e) {
            ObjectNode errorReport = new ErrorReport().makeErrorReport("MaxUploadSizeExceededException", e.getMessage());
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorReport);
        }
    }

    @RestControllerAdvice(assignableTypes = PostController.class)
    public static class UnsupportedImageExtensionExceptionHandler {
        @ExceptionHandler(UnsupportedImageExtensionException.class)
        public ResponseEntity<?> unSupportedImageExtensionException(UnsupportedImageExtensionException e) {
            ObjectNode errorReport = new ErrorReport().makeErrorReport("UnsupportedImageExtensionException", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorReport);
        }
    }

    @RestControllerAdvice(assignableTypes = RetrieveClosePostController.class)
    public static class MissingServletRequestParameterExceptionHandler{
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<?> MissingParameterException(Exception e) {
            ObjectNode errorReport = new ErrorReport().makeErrorReport("MissingServletRequestParameterException", e.getMessage());
            return ResponseEntity.badRequest().body(errorReport);
        }
    }
}