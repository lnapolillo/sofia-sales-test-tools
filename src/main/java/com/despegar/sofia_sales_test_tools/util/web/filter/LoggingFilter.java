package com.despegar.sofia_sales_test_tools.util.web.filter;

import com.despegar.sofia_sales_test_tools.util.web.filter.wrapper.CachedContentRequestWrapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LoggingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    private final List<String> ignoredPaths;
    private final List<String> noBodyPaths;

    public LoggingFilter(List<String> ignoredPaths, List<String> noBodyPaths) {
        this.ignoredPaths = ignoredPaths;
        this.noBodyPaths = noBodyPaths.stream().filter(s -> !s.isBlank()).toList();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    private boolean shouldLog(HttpServletRequest request) {
        return ignoredPaths.stream()
                .noneMatch(ignoredPath -> request.getRequestURI().toLowerCase().endsWith(ignoredPath));
    }

    private boolean shouldLogBody(HttpServletRequest request) {
        return noBodyPaths.stream()
                .noneMatch(path -> request.getRequestURI().startsWith(path));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        if (!shouldLog(httpServletRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        long startTime = System.currentTimeMillis();
        ContentCachingResponseWrapper httpResponse = new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);

        if (shouldLogBody(httpServletRequest)) {
            CachedContentRequestWrapper httpRequest = new CachedContentRequestWrapper(httpServletRequest);
            logRequest(httpRequest);
            try {
                filterChain.doFilter(httpRequest, httpResponse);
            } finally {
                logResponse(httpServletRequest, httpResponse, startTime);
                httpResponse.copyBodyToResponse();
            }
        } else {
            logRequestMetadata(httpServletRequest);
            try {
                filterChain.doFilter(servletRequest, httpResponse);
            } finally {
                logResponseMetadata(httpServletRequest, httpResponse, startTime);
                httpResponse.copyBodyToResponse();
            }
        }
    }

    private void logRequest(HttpServletRequest request) throws IOException {
        String queryString = (StringUtils.isBlank(request.getQueryString()) ? "" : "?") + request.getQueryString();
        String headers = buildHeaders(Collections.list(request.getHeaderNames()), request::getHeader);
        String body = toString(request.getInputStream());
        LOGGER.info("[REQUEST] -> {}: {}{} - body = {} - headers = {}", request.getMethod(), request.getRequestURI(), queryString, body, headers);
    }

    private void logRequestMetadata(HttpServletRequest request) {
        String queryString = (StringUtils.isBlank(request.getQueryString()) ? "" : "?") + request.getQueryString();
        LOGGER.info("[REQUEST] -> {}: {}{}", request.getMethod(), request.getRequestURI(), queryString);
    }

    private void logResponse(HttpServletRequest request, ContentCachingResponseWrapper response, long startTime) {
        LOGGER.info("[RESPONSE] -> -> {}: {} - status = {} - body: {} - spent {} millis", request.getMethod(), request.getRequestURI(), response.getStatus(), new String(response.getContentAsByteArray()), System.currentTimeMillis() - startTime);
    }

    private void logResponseMetadata(HttpServletRequest request, ContentCachingResponseWrapper response, long startTime) {
        LOGGER.info("[RESPONSE] -> -> {}: {} - status = {} - spent {} millis", request.getMethod(), request.getRequestURI(), response.getStatus(), System.currentTimeMillis() - startTime);
    }

    private String toString(InputStream inputStream) throws IOException {
        return StringUtils.replace(StringUtils.trimToEmpty(IOUtils.toString(inputStream, Charset.defaultCharset())), "\\r\\n|\\r|\\n", " ");
    }

    private String buildHeaders(List<String> headerNames, Function<String, String> headerExtractor) {
        return headerNames.stream().map(h -> new StringBuilder("'").append(h).append(":").append(headerExtractor.apply(h)).append("'")).collect(Collectors.joining());
    }

    @Override
    public void destroy() {
    }
}
