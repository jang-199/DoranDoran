package com.dorandoran.doranserver.controller.aop;

import com.dorandoran.doranserver.dto.InquiryDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Aspect
@Slf4j
@Component
public class InquiryAspect {

    @Around("execution(* com.dorandoran.doranserver.controller..*Inquiry*(..))")
    public Object returnErrorLog(ProceedingJoinPoint joinPoint) {
        try {
            log.info("[aop] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }catch (Throwable e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
