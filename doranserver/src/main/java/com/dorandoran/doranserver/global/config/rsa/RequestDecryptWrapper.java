package com.dorandoran.doranserver.global.config.rsa;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.text.ParseException;

@Slf4j
public class RequestDecryptWrapper extends HttpServletRequestWrapper {

    private final String requestDecryptBody;
    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public RequestDecryptWrapper(HttpServletRequest request, PrivateKey privateKey) throws IOException, ParseException, JOSEException {
        super(request);

        String requestDataByte = requestDataByte(request);

        requestDecryptBody = requestBodyDecode(privateKey, requestDataByte);

    }

    private String requestDataByte(HttpServletRequest request) throws IOException {
        byte[] rawData;
        InputStream inputStream = request.getInputStream();
        rawData = inputStream.readAllBytes();
        return new String(rawData);
    }

    private String requestBodyDecode(PrivateKey privateKey, String requestEncodedData) throws ParseException, JOSEException {

        JWEObject parse = JWEObject.parse(requestEncodedData);
        parse.decrypt(new RSADecrypter(privateKey));
        String decryptedString = parse.getPayload().toString();
        log.info("복호화 결과: {}", decryptedString);
        return decryptedString;
    }
}
