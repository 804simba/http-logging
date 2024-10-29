package com.simba.quartz.aspect;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class CoreAspect {
    @Value("${spring.application.name}")
    private String serviceName;

    private Tracer tracer;

    @PostConstruct
    public void init() {
        this.tracer = GlobalOpenTelemetry.getTracer(serviceName);
    }

    @Pointcut("!within(com.simba.quartz.filters..*)")
    public void notCustomFilters() {}

    @Pointcut("!within(com.simba.quartz.auth..*)")
    public void notAuthFilter() {}

    @AfterThrowing(pointcut = "execution(* com.simba.quartz..*.*(..)) && notCustomFilters() && notAuthFilter()", throwing = "ex")
    public void logExceptionToSpan(Exception ex) {
        Span currentSpan = Span.current();
        if (currentSpan != null && currentSpan.isRecording()) {
            log.info("Recording exception in current span: {}", currentSpan.getSpanContext().getSpanId());
            logErrorToSpan(currentSpan, ex);
        } else {
            log.warn("No active span found for exception logging.");
        }
    }

    @Around("@annotation(com.simba.quartz.aspect.Traceable) && notCustomFilters() && notAuthFilter()")
    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String spanName = getSpanName(signature);

        Span span = tracer.spanBuilder(spanName).startSpan();
        try (Scope ignored = span.makeCurrent()) {
            addParametersToSpan(span, signature, joinPoint.getArgs());

            Object result = joinPoint.proceed();

            if (result != null) {
                span.setAttribute("response.type", result.getClass().getSimpleName());
            }

            return result;
        } catch (Exception e) {
            logErrorToSpan(span, e);
            throw e;
        } finally {
            span.end();
        }
    }

    private String getSpanName(MethodSignature signature) {
        Traceable annotation = signature.getMethod().getAnnotation(Traceable.class);
        String value = annotation.value();

        return value.isEmpty()
                ? String.format("%s.%s", signature.getDeclaringType().getSimpleName(), signature.getName())
                : value;
    }

    private void addParametersToSpan(Span span, MethodSignature signature, Object[] args) {
        String[] parameterNames = signature.getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            if (args[i] != null) {
                String key = "parameter." + parameterNames[i];
                Object value = args[i];

                if (value instanceof String) {
                    span.setAttribute(AttributeKey.stringKey(key), (String) value);
                } else if (value instanceof Long) {
                    span.setAttribute(AttributeKey.longKey(key), (Long) value);
                } else if (value instanceof Integer) {
                    span.setAttribute(AttributeKey.longKey(key), ((Integer) value).longValue());
                } else if (value instanceof Double) {
                    span.setAttribute(AttributeKey.doubleKey(key), (Double) value);
                } else if (value instanceof Boolean) {
                    span.setAttribute(AttributeKey.booleanKey(key), (Boolean) value);
                } else {
                    span.setAttribute(AttributeKey.stringKey(key), value.toString());
                }
                log.info("Set span attribute {} = {}", key, value);
            }
        }
    }

    public static void logErrorToSpan(Span span, Exception e) {
        span.setAttribute("error", true);
        span.setAttribute("exception.type", e.getClass().getName());
        span.setAttribute("exception.message", e.getMessage());
        span.setAttribute("stacktrace", Arrays.toString(e.getStackTrace()));
        span.recordException(e);
        span.setStatus(StatusCode.ERROR, e.getMessage());
        String stackTrace =
                Arrays.stream(e.getStackTrace())
                        .map(element -> element.toString() + "\n")
                        .collect(Collectors.joining());
        span.addEvent(
                "stack_trace", Attributes.builder().put("exception.stacktrace", stackTrace).build());
    }
}
