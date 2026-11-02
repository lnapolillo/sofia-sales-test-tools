package com.despegar.sofia_sales_test_tools.util.web.filter;

import com.despegar.sofia_sales_test_tools.util.Constants;
import com.despegar.library.routing.RSD;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AccessFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessFilter.class);

    /**
     * Default maximum number of bytes to cache for DEBUG body logging (1 MB).
     * Use -1 for unlimited caching, but be aware this may cause high memory usage for large request/response bodies.
     */
    public static final int DEFAULT_CONTENT_CACHE_LIMIT = 1024 * 1024;

    private final List<String> ignoredPaths;

    public AccessFilter(List<String> ignoredPaths) {
        this.ignoredPaths = ignoredPaths;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        final var httpRequest = (HttpServletRequest) request;

        saveInContextHeaderOrDefault(httpRequest, Constants.REFERER, Constants.UNKNOWN);
        saveInContextHeaderOrDefault(httpRequest, Constants.X_CLIENT, Constants.UNKNOWN);
        saveInContextHeaderOrDefault(httpRequest, Constants.X_FORWARDED_FOR, httpRequest.getRemoteAddr());

        if (shouldLog(httpRequest)) {
            logRequestInitialInfo(httpRequest);
            if (LOGGER.isDebugEnabled()) {
                // -1 means no cache limit. If there is a limit some filter may get just a part of the request,
                // will not work and will be horrible to understand why
                final var requestWrapper = new ContentCachingRequestWrapper(httpRequest, -1);
                final var responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);
                filterChain.doFilter(requestWrapper, responseWrapper);
                this.logBodies(requestWrapper, responseWrapper);
            } else {
                filterChain.doFilter(request, response);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig arg0) {
        LOGGER.info("Initializing AccessLogFilter");
    }

    @Override
    public void destroy() {
        LOGGER.info("Destroying AccessLogFilter");
    }

    private void saveInContextHeaderOrDefault(HttpServletRequest request, String headerKey, String defaultValue) {
        final var headerValue = Optional.ofNullable(request.getHeader(headerKey)).orElse(defaultValue);
        RSD.put(headerKey, headerValue);
    }

    private boolean shouldLog(HttpServletRequest request) {
        return ignoredPaths.stream()
                .noneMatch(ignoredPath -> request.getRequestURI().toLowerCase().endsWith(ignoredPath));
    }

    private void logRequestInitialInfo(HttpServletRequest request) {
        try {
            final var requestUrl = request.getRequestURL().toString();
            final var queryString = request.getQueryString();
            final var method = request.getMethod();
            final var headers = extractHeaders(request);

            LOGGER.info("Request -> {} -> {} {} -> Querystring: {}", method, requestUrl, headers,
                    queryString);

        } catch (Exception ex) {
            LOGGER.warn("Error trying log initial request info. ", ex);
        }
    }

    private String extractHeaders(HttpServletRequest request) {
        final var builder = new StringBuilder("Headers: ");

        Collections.list(request.getHeaderNames()).stream()
                .filter(name -> !isAuthorizationHeader(name))
                .forEach(headerName -> builder.append("%s=%s|".formatted(headerName, request.getHeader(headerName))));

        return builder.toString();
    }

    private boolean isAuthorizationHeader(String headerName) {
        return Constants.AUTHORIZATION_HEADER.equalsIgnoreCase(headerName);
    }

    private void logBodies(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper)
            throws IOException {
        try {
            final var requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            LOGGER.debug("Request body: {}", requestBody);
            final var responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            LOGGER.debug("Response body: {}", responseBody);
        } catch (Exception e) {
            LOGGER.warn("Error trying log request and response bodies. ", e);
        } finally {
            responseWrapper.copyBodyToResponse();
        }

    }

}
