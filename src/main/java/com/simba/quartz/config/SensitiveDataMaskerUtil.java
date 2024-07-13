package com.simba.quartz.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SensitiveDataMaskerUtil extends CompositeConverter<ILoggingEvent> {
    private static final String MASK = "********";
    private static final Pattern JSON_PATTERN = Pattern.compile("\\{[^}]+}");
    private static final Pattern SENSITIVE_FIELD_PATTERN;
    private static final Logger logger = LoggerFactory.getLogger(SensitiveDataMaskerUtil.class);

    static {
        Pattern tempPattern;
        try {
            Properties props = PropertiesLoaderUtils.loadProperties(new ClassPathResource("application.properties"));
            String sensitiveFields = props.getProperty("sensitive.fields");
            String patternString = Arrays.stream(sensitiveFields.split(","))
                    .map(String::trim)
                    .collect(Collectors.joining("|", "\"(", ")\"\\s*:\\s*\"([^\"]+)\""));
            tempPattern = Pattern.compile(patternString);
        } catch (IOException e) {
            logger.warn("Error loading sensitive fields: {}", e.getMessage());
            tempPattern = Pattern.compile("\"(emailAddress|phoneNumber|password|otp|token)\"\\s*:\\s*\"([^\"]+)\"");
        }
        SENSITIVE_FIELD_PATTERN = tempPattern;
    }

    @Override
    protected String transform(ILoggingEvent event, String in) {
        Matcher jsonMatcher = JSON_PATTERN.matcher(in);
        StringBuilder builder = new StringBuilder();

        while (jsonMatcher.find()) {
            String json = jsonMatcher.group();
            json = maskSensitiveFields(json);
            jsonMatcher.appendReplacement(builder, Matcher.quoteReplacement(json));
        }
        jsonMatcher.appendTail(builder);

        return builder.toString();
    }

    private String maskSensitiveFields(String json) {
        Matcher matcher = SENSITIVE_FIELD_PATTERN.matcher(json);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String replacement = String.format("\"%s\":\"%s\"", matcher.group(1), MASK);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
