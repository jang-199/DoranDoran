//package com.dorandoran.doranserver.global.config.rsa;
//
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Slf4j
//@WebFilter(urlPatterns = {"*/hashTag",
//        "*/comment",
//        "*/comment/like",
//        "*/reply",
//        "*/post",
//        "*/post/detail",
//        "*/post/like",
//        "*/post/close",
//        "*/post/popular",
//        "*/post/hashTag",
//        "*/post/report",
//        "*/reply/report",
//        "*/comment/report",
//        "*/token",
//        "*/member",
//        "*/nickname",
//        "*/registered",
//        "*/member/block",
//        "*/inquiryPost"})
//@Component
//@RequiredArgsConstructor
//public class RsaEncoderFilter implements Filter {
//    private final RsaProperties rsaProperties;
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        RequestDecryptWrapper requestDecryptWrapper = new RequestDecryptWrapper((HttpServletRequest) request, rsaProperties);
//        chain.doFilter(requestDecryptWrapper,response);
//
//    }
//
//}
