package com.ticonsys.online_meet.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
@NoArgsConstructor
public class ApplicationLoggingInterceptor implements HandlerInterceptor {
    private static final String REQUEST_ID = "requestId";
    private static final ThreadLocalRandom SECURE_RANDOM = ThreadLocalRandom.current();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = generateRequestId();
        MDC.put(REQUEST_ID, requestId);
        log.info("Received request [ID: {}, Method: {}, URI: {}]", requestId, request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String requestId = MDC.get(REQUEST_ID);
        if (!Objects.isNull(ex))
            log.error("Request [ID: {}] completed with error [Status: {}, URI: {}]", requestId, response.getStatus(), request.getRequestURI(), ex);
        else
            log.info("Request [ID: {}] completed [Status: {}, URI: {}]", requestId, response.getStatus(), request.getRequestURI());
        MDC.remove(REQUEST_ID);
    }

    private String generateRequestId() {
        byte[] randomBytes = new byte[16];
        SECURE_RANDOM.nextBytes(randomBytes);
        return new BigInteger(1, randomBytes).toString(16).substring(0, 8);
    }
}
