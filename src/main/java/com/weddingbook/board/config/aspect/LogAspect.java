package com.weddingbook.board.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogAspect {
    @Around("execution(* com.weddingbook.board.controller..*.*(..))")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("start - " + proceedingJoinPoint.getSignature().getDeclaringTypeName() + " / " + proceedingJoinPoint.getSignature().getName());
        Object result = proceedingJoinPoint.proceed();
        log.info("finished - " + proceedingJoinPoint.getSignature().getDeclaringTypeName() + " / " + proceedingJoinPoint.getSignature().getName());

        return result;
    }
}
