package com.dorandoran.doranserver.global.config.rsa;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;

@Slf4j
@WebFilter
@Component
@RequiredArgsConstructor
public class RsaEncoderFilter implements Filter {
    private final RsaProperties rsaProperties;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            RequestDecryptWrapper requestDecryptWrapper = new RequestDecryptWrapper((HttpServletRequest) request, rsaProperties.getPRIVATE_KEY());
            chain.doFilter(requestDecryptWrapper,response);

        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }

}
