package com.dorandoran.doranserver.controller;

import com.dorandoran.doranserver.dto.SignUpDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller
public class MainController {
    @PostMapping("/api/signup")
    ResponseEntity<?> mainPage(@RequestBody SignUpDto signUpDto) {
        log.info("NickName : {} , DateOfBirth : {} , IdentificationNumber : {}",signUpDto.getNickName(),signUpDto.getDateOfBirth(),signUpDto.getIdentificationNumber());
        return new ResponseEntity<>(signUpDto,HttpStatus.OK);
    }
}
