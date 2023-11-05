package com.dorandoran.doranserver.domain.rsa.service;

import com.dorandoran.doranserver.domain.rsa.domain.RsaKey;
import com.dorandoran.doranserver.domain.rsa.repository.RsaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.*;
import java.util.Base64;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class RsaService {
    private final RsaRepository rsaRepository;
    public void saveKey(PublicKey publicKey, PrivateKey privateKey){
        RsaKey build = RsaKey.builder()
                .PUBLIC_KEY(Base64.getEncoder().encodeToString(publicKey.getEncoded()))
                .PRIVATE_KEY(Base64.getEncoder().encodeToString(privateKey.getEncoded()))
                .build();
        rsaRepository.save(build);
    }


    @Scheduled(cron = "0 0 */6 * * *", zone = "Asia/Seoul")
    public void updateKey() throws NoSuchAlgorithmException {
        KeyPair keyPair = generateKeyPair();
        Optional<RsaKey> byId = rsaRepository.findById(1L);
        if (byId.isEmpty()) {
            throw new RuntimeException("저장된 rsa key가 없어 업데이트할 수 없습니다.");
        }
        RsaKey rsaKey = byId.get();
        rsaKey.setPUBLIC_KEY(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        rsaKey.setPRIVATE_KEY(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
//            rsaRepository.save(rsaKey);
    }


    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
        rsa.initialize(2048);
        return rsa.generateKeyPair();
    }

    public RsaKey findKey() {
        return rsaRepository.findById(1L).orElseThrow(() -> new RuntimeException("미리 생성된 rsa key가 없습니다."));
    }

    public boolean isEmpty() {
        return rsaRepository.findById(1L).isEmpty();
    }
}
