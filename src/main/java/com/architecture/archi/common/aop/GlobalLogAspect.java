package com.architecture.archi.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Component
@Aspect
public class GlobalLogAspect {

    // 컨트롤러 패키지 내 메소드 호출 시
    @Around("execution(* com.architecture.archi.content..controller..*(..))")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        // 컨트롤러 메소드 호출 이전
        LocalDateTime now = LocalDateTime.now();

        Object joinPointResult = null;

        try {
            // HttpServletRequest에서 요청 관련 정보 가져오기
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            log.info("{} [CONTROLLER] Controller Start", now);

            log.info("{} [CONTROLLER] | Session: {}", now, request.getSession().getId());
            log.info("{} [CONTROLLER] | userAgent: {}", now, request.getHeader("User-Agent"));
            log.info("{} [CONTROLLER] | protocol: {}", now, request.getProtocol());

            String ip = request.getHeader("X-FORWARDED-FOR");

            //proxy 환경일 경우
            if (ip == null || ip.length() == 0) {
                ip = request.getHeader("Proxy-Client-IP");
            }

            //웹로직 서버일 경우
            if (ip == null || ip.length() == 0) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }

            if (ip == null || ip.length() == 0) {
                ip = request.getRemoteAddr() ;
            }

            log.info("{} [CONTROLLER] | Method: /{}, URI:{}, Ip addr: {}", now, request.getMethod(), request.getRequestURI(), ip);

            // 컨트롤러 메소드 호출
            joinPointResult = joinPoint.proceed();
        } catch (IllegalStateException illegalStateException) {
            // HttpServletRequest 상태가 비어 있을 때
            log.info("{} [CONTROLLER] | No thread-bound request found.", now);
        } finally {
            // 컨트롤러 메소드 호출 이후
            log.info("{} [CONTROLLER] | Controller End", now);
        }

        return joinPointResult;
    }

    // 예외 발생 시
    @AfterThrowing(pointcut = "execution(* com.architecture..*(..))", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e) {
        LocalDateTime now = LocalDateTime.now();
        log.error("{} [Throw] | Message: {}", now, e.getMessage());
    }

    // 비동기 메소드 호출 시
//    @Before("@annotation(org.springframework.scheduling.annotation.Async)")
//    public void beforeAsyncMethod(JoinPoint joinPoint) {
//        LocalDateTime now = LocalDateTime.now();
//        log.info("{} [Async] | Run Async Method: {}", now, joinPoint.getTarget());
//    }
}