package com.dorandoran.doranserver.global.util.aop;

import com.dorandoran.doranserver.domain.post.dto.PostDto;
import com.dorandoran.doranserver.global.config.rsa.RsaProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSAEncrypter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class EncryptAspect {
    private final RsaProperties rsaProperties;

    @Around("execution(* com.dorandoran.doranserver.domain.*.controller..*(..))")
    public ResponseEntity<?> responseEncrypt(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String headerPublicKey = request.getHeader("Public-Key");

        PublicKey publicKey = publicKeyGenerator(headerPublicKey);

        ResponseEntity<?> result = (ResponseEntity<?>) joinPoint.proceed();

        if (!isEncryptCondition(result.getStatusCode())){
            return result;
        }

        Object responseEntityBody = result.getBody();

        String responseJson = ObjectToJson(responseEntityBody);
        String responseEncryptData = encryptData(responseJson, publicKey);

        return ResponseEntity.status(result.getStatusCode()).body(responseEncryptData);
    }

    private static PublicKey publicKeyGenerator(String headerPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory rsa1 = KeyFactory.getInstance("RSA");
        byte[] decode = Base64.getDecoder().decode(headerPublicKey);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(decode);
        return rsa1.generatePublic(x509EncodedKeySpec);
    }

    private Boolean isEncryptCondition(HttpStatusCode httpStatusCode){
        return httpStatusCode.equals(HttpStatus.OK);
    }

    private static String ObjectToJson(Object responseEntityBody) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(responseEntityBody);
    }

    private String encryptData(String responseJson, PublicKey publicKey) throws JOSEException {
        JWEObject jwt = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM)
                        .contentType("JWT")
                        .build(),
                new Payload(responseJson)
        );

        jwt.encrypt(new RSAEncrypter((RSAPublicKey) publicKey));
        return jwt.serialize();
    }
}
