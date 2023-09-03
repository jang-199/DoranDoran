package com.dorandoran.doranserver.domain.customerservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class InquiryAspect {

    @Around("execution(* com.dorandoran.doranserver.domain.customerservice.controller..*Inquiry*(..))")
    public Object returnErrorLog(ProceedingJoinPoint joinPoint) {
        try {
            log.info("[aop] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }catch (Throwable e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
