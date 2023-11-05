package com.dorandoran.doranserver.global.config.rsa;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;

@Slf4j
public class RequestDecryptWrapper extends HttpServletRequestWrapper {
    private String decodingBody;
    private RsaProperties rsaProperties;

    public RequestDecryptWrapper(HttpServletRequest request, RsaProperties rsaProperties) {
        super(request);
        this.rsaProperties = rsaProperties;

        try {
            InputStream inputStream = request.getInputStream();
//            rawData = IOUtils.toByteArray(inputStream);
            byte[] rawData = convertInputStreamToByteArray(inputStream);

            if (ObjectUtils.isEmpty(rawData)) {
                return;
            }

            JWEObject parse = JWEObject.parse(new String(rawData, StandardCharsets.UTF_8));
            parse.decrypt(new RSADecrypter(rsaProperties.getPRIVATE_KEY()));
            String decryptedString = parse.getPayload().toString();
            this.decodingBody = decryptedString;

            log.info("복호화 결과: {}", decryptedString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decodingBody == null ? "".getBytes(StandardCharsets.UTF_8) : decodingBody.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter); // 전달받은 parameter 불러오기

        if(value == null) {
            return null;
        }

        try {
            JWEObject parse = JWEObject.parse(new String(value.getBytes(), StandardCharsets.UTF_8));
            parse.decrypt(new RSADecrypter(rsaProperties.getPRIVATE_KEY()));
            value = parse.getPayload().toString();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String values[] = super.getParameterValues(name); // 전달받은 parameter 불러오기

        if(values == null) {
            return null;
        }

        for(int i=0; i<values.length; i++) {
            if (values[i] != null) {
                try {
                    JWEObject parse = JWEObject.parse(new String(values[i].getBytes(), StandardCharsets.UTF_8));
                    parse.decrypt(new RSADecrypter(rsaProperties.getPRIVATE_KEY()));
                    values[i] = parse.getPayload().toString();
                    log.info("실행됨");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return values;
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    private static byte[] convertInputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
