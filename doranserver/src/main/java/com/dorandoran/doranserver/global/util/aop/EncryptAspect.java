package com.dorandoran.doranserver.global.util.aop;

import com.dorandoran.doranserver.domain.post.dto.PostDto;
import com.dorandoran.doranserver.global.config.rsa.RsaProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSAEncrypter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.security.interfaces.RSAPublicKey;


@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class EncryptAspect {
    private final RsaProperties rsaProperties;

    @Around("execution(* com.dorandoran.doranserver.domain.*.controller..*(..))")
    public ResponseEntity<?> responseEncrypt(ProceedingJoinPoint joinPoint) throws Throwable {
        ResponseEntity<?> result = (ResponseEntity<?>) joinPoint.proceed();

        if (!isEncryptCondition(result.getStatusCode())){
            return result;
        }

        PostDto.ReadPostResponse responseEntityBody = (PostDto.ReadPostResponse) result.getBody();

        String responseJson = ObjectToJson(responseEntityBody);
        String responseEncryptData = encryptData(responseJson);

        return ResponseEntity.status(result.getStatusCode()).body(responseEncryptData);
    }

    private Boolean isEncryptCondition(HttpStatusCode httpStatusCode){
        return httpStatusCode.equals(HttpStatus.OK);
    }

    private static String ObjectToJson(PostDto.ReadPostResponse responseEntityBody) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(responseEntityBody);
    }

    private String encryptData(String responseJson) throws JOSEException {
        JWEObject jwt = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM)
                        .contentType("JWT")
                        .build(),
                new Payload(responseJson)
        );

        jwt.encrypt(new RSAEncrypter((RSAPublicKey) rsaProperties.getPUBLIC_KEY()));
        return jwt.serialize();
    }
}
