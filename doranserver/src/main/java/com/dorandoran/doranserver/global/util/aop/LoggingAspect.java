package com.dorandoran.doranserver.global.util.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Slf4j
@Component
public class LoggingAspect {
    @Before("@annotation(com.dorandoran.doranserver.global.util.annotation.Trace)")
    public void logging(JoinPoint joinPoint) {
        String[] className = joinPoint.getSignature().getName().split(" ");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.info("ip : {}, {}: {}", request.getRemoteAddr(), authentication.getName(), className[className.length - 1]);
    }
}