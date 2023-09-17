package com.dorandoran.doranserver.global.config.jasypt;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {


    @Bean("jasyptEncryptor")
    public StringEncryptor encryptor() {
        PooledPBEStringEncryptor pooledPBEStringEncryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig simpleStringPBEConfig = new SimpleStringPBEConfig();
        simpleStringPBEConfig.setPassword(System.getProperty("jasypt.encryptor.password"));
        simpleStringPBEConfig.setAlgorithm("PBEWithMD5AndDES");
        simpleStringPBEConfig.setKeyObtentionIterations(1000);
        simpleStringPBEConfig.setPoolSize(4);
        pooledPBEStringEncryptor.setConfig(simpleStringPBEConfig);
        return pooledPBEStringEncryptor;
    }

}
