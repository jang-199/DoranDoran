package com.dorandoran.doranserver.controller.exception;

import com.dorandoran.doranserver.controller.PostController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice(assignableTypes = PostController.class)
public class ExceptionController {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> fileUploadException(Exception e){
        log.info("파일 업로드 크기 제한 exception",e);
        return new ResponseEntity<>("파일 업로드 크기 제한", HttpStatus.BAD_REQUEST);
    }
}
