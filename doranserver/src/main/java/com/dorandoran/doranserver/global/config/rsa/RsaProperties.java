package com.dorandoran.doranserver.global.config.rsa;

import com.dorandoran.doranserver.domain.rsa.domain.RsaKey;
import com.dorandoran.doranserver.domain.rsa.service.RsaService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class RsaProperties {
    private PrivateKey PRIVATE_KEY;
    private PublicKey PUBLIC_KEY;

    private final RsaService rsaService;

    public void generateKey() throws NoSuchAlgorithmException {
        KeyPair keyPair = rsaService.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        PUBLIC_KEY = publicKey;
        PRIVATE_KEY = privateKey;

        rsaService.saveKey(publicKey,privateKey);
    }

    public void reloadKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        RsaKey key = rsaService.findKey();
        byte[] publicByte = Base64.getDecoder().decode(key.getPUBLIC_KEY());
        byte[] privateByte = Base64.getDecoder().decode(key.getPRIVATE_KEY());

        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicByte);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateByte);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        PUBLIC_KEY = publicKey;
        PRIVATE_KEY = privateKey;
    }

}
