package com.simba.quartz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoggingInterceptor extends HttpFilter {

    private final ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    public LoggingInterceptor() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.setDateFormat(new StdDateFormat());
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        try {
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

            chain.doFilter(wrappedRequest, wrappedResponse);

            logRequest(wrappedRequest);
            logResponse(wrappedResponse);

            wrappedResponse.copyBodyToResponse();
        } catch (Exception e) {
            System.out.println("ERROR " +e.getMessage());
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) throws Exception {
        String body = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        Map<String, String> headers = getHeadersInfo(request);
        logger.info("Request: {} {} Body: {}", request.getMethod(), request.getRequestURI(), prettyPrintJson(body));
        logger.info("IP Address: {}", ipAddress);
        logger.info("User-Agent: {}", userAgent);
        logger.info("Headers: {}", prettyPrintJson(headers));
    }

    private void logResponse(ContentCachingResponseWrapper response) {
        try {
            String body = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
            logger.info("Response: {} Body: {}", response.getStatus(), prettyPrintJson(body));
        } catch (Exception e) {
            logger.warn("ERROR {}", e.getMessage());
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
        return headers;
    }

    private String prettyPrintJson(Object json) {
        try {
            if (json instanceof String payload) {
                Object jsonObject = objectMapper.readValue(payload, Object.class);
                // Format the parsed JSON object
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            }
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (IOException e) {
            // If the input is not a valid JSON, return it as is
            return json.toString();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
