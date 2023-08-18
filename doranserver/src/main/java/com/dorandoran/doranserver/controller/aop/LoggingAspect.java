package com.dorandoran.doranserver.controller.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LoggingAspect {
    @Before("@annotation(com.dorandoran.doranserver.controller.annotation.Trace)")
    public void logging(JoinPoint joinPoint) {
        String[] className = joinPoint.getSignature().getName().split(" ");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("{}: {}", authentication.getName(), className[className.length - 1]);
    }
}