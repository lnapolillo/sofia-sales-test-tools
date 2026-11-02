package com.despegar.sofia_sales_test_tools.util.web.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.despegar.sofia_sales_test_tools.util.Constants;
import com.despegar.library.routing.RSD;
import com.google.common.collect.Iterators;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class AccessFilterTest {

    private static final String IGNORED_PATH = "/ignored";
    private static final String REFERER = "referer";
    private static final String X_FORWARDED_FOR = "x-forwarded-for";
    private static final String X_CLIENT = "client";
    private static final Appender<ILoggingEvent> MOCKED_APPENDER = (Appender<ILoggingEvent>) Mockito
            .mock(Appender.class);

    private static final String URL = "url";
    private static final String QUERY_STRING = "queryString";
    private static final String METHOD = "method";
    private static final String HEADER_KEY1 = "header1";
    private static final String HEADER_VALUE1 = "headerValue1";
    private static final String HEADER_KEY2 = "header2";
    private static final String HEADER_VALUE2 = "headerValue2";

    @Captor
    private ArgumentCaptor<LoggingEvent> loggingEventCaptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private StringBuffer urlStringBuffer;

    private final AccessFilter accessFilter = new AccessFilter(List.of(IGNORED_PATH));

    @BeforeEach
    void init() {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(MOCKED_APPENDER);
        root.setLevel(Level.DEBUG);
        accessFilter.init(null);
        reset(MOCKED_APPENDER);
        RSD.clear();
    }

    @AfterEach
    void destroy() {
        accessFilter.destroy();
    }

    @Test
    void doFilter_notShouldLog() throws IOException, ServletException {
        when(request.getHeader(Constants.REFERER)).thenReturn(REFERER);
        when(request.getHeader(Constants.X_CLIENT)).thenReturn(X_CLIENT);
        when(request.getHeader(Constants.X_FORWARDED_FOR)).thenReturn(X_FORWARDED_FOR);
        when(request.getRequestURI()).thenReturn(IGNORED_PATH);

        accessFilter.doFilter(request, response, filterChain);

        assertEquals(REFERER, RSD.get(Constants.REFERER));
        assertEquals(X_CLIENT, RSD.get(Constants.X_CLIENT));
        assertEquals(X_FORWARDED_FOR, RSD.get(Constants.X_FORWARDED_FOR));
        verify(request, times(1)).getHeader(Constants.REFERER);
        verify(request, times(1)).getHeader(Constants.X_CLIENT);
        verify(request, times(1)).getHeader(Constants.X_FORWARDED_FOR);
        verify(request, times(1)).getRequestURI();
        verifyNoInteractions(response);
        verify(filterChain, only()).doFilter(request, response);
        verifyNoInteractions(MOCKED_APPENDER);
        assertTrue(loggingEventCaptor.getAllValues().isEmpty());
    }

    @Test
    void doFilter_defaults() throws IOException, ServletException {
        final var addr = "addr";

        when(request.getHeader(Constants.REFERER)).thenReturn(null);
        when(request.getHeader(Constants.X_CLIENT)).thenReturn(null);
        when(request.getHeader(Constants.X_FORWARDED_FOR)).thenReturn(null);
        when(request.getRequestURI()).thenReturn(IGNORED_PATH);
        when(request.getRemoteAddr()).thenReturn(addr);

        accessFilter.doFilter(request, response, filterChain);

        assertEquals(Constants.UNKNOWN, RSD.get(Constants.REFERER));
        assertEquals(Constants.UNKNOWN, RSD.get(Constants.X_CLIENT));
        assertEquals(addr, RSD.get(Constants.X_FORWARDED_FOR));
        verify(request, times(1)).getHeader(Constants.REFERER);
        verify(request, times(1)).getHeader(Constants.X_CLIENT);
        verify(request, times(1)).getHeader(Constants.X_FORWARDED_FOR);
        verify(request, times(1)).getRequestURI();
        verifyNoInteractions(response);
        verify(filterChain, only()).doFilter(request, response);
        verifyNoInteractions(MOCKED_APPENDER);
        assertTrue(loggingEventCaptor.getAllValues().isEmpty());
    }

    @Test
    void doFilter_shouldLog() throws IOException, ServletException {
        when(request.getHeader(Constants.REFERER)).thenReturn(REFERER);
        when(request.getHeader(Constants.X_CLIENT)).thenReturn(X_CLIENT);
        when(request.getHeader(Constants.X_FORWARDED_FOR)).thenReturn(X_FORWARDED_FOR);
        when(request.getRequestURI()).thenReturn("/should-log");

        when(request.getRequestURL()).thenReturn(urlStringBuffer);
        when(urlStringBuffer.toString()).thenReturn(URL);
        when(request.getQueryString()).thenReturn(QUERY_STRING);
        when(request.getMethod()).thenReturn(METHOD);
        when(request.getHeaderNames())
                .thenReturn(Iterators.asEnumeration(List.of(HEADER_KEY1, HEADER_KEY2).iterator()));
        when(request.getHeader(HEADER_KEY1)).thenReturn(HEADER_VALUE1);
        when(request.getHeader(HEADER_KEY2)).thenReturn(HEADER_VALUE2);

        accessFilter.doFilter(request, response, filterChain);

        assertEquals(REFERER, RSD.get(Constants.REFERER));
        assertEquals(X_CLIENT, RSD.get(Constants.X_CLIENT));
        assertEquals(X_FORWARDED_FOR, RSD.get(Constants.X_FORWARDED_FOR));
        verify(request, times(1)).getHeader(Constants.REFERER);
        verify(request, times(1)).getHeader(Constants.X_CLIENT);
        verify(request, times(1)).getHeader(Constants.X_FORWARDED_FOR);
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getRequestURL();
        verify(request, times(1)).getQueryString();
        verify(request, times(1)).getMethod();
        verify(request, times(1)).getHeaderNames();
        verify(request, times(1)).getHeader(HEADER_KEY1);
        verify(request, times(1)).getHeader(HEADER_KEY2);
        verifyNoInteractions(response);
        verify(filterChain, only()).doFilter(any(ContentCachingRequestWrapper.class),
                any(ContentCachingResponseWrapper.class));

        verify(MOCKED_APPENDER, atLeastOnce()).doAppend(loggingEventCaptor.capture());
        final var logEvents = loggingEventCaptor.getAllValues();
        assertEquals(3, logEvents.size());
        final var expectedHeadersLog = "Headers: %s=%s|%s=%s|".formatted(HEADER_KEY1, HEADER_VALUE1, HEADER_KEY2,
                HEADER_VALUE2);
        final var expectedLogLine = "Request -> %s -> %s %s -> Querystring: %s".formatted(METHOD,
                URL, expectedHeadersLog, QUERY_STRING);
        assertEquals(expectedLogLine, logEvents.get(0).getFormattedMessage());
        assertEquals("Request body: ", logEvents.get(1).getFormattedMessage());
        assertEquals("Response body: ", logEvents.get(2).getFormattedMessage());
    }

    @Test
    void doFilter_onException() throws IOException, ServletException {
        when(request.getHeader(Constants.REFERER)).thenReturn(REFERER);
        when(request.getHeader(Constants.X_CLIENT)).thenReturn(X_CLIENT);
        when(request.getHeader(Constants.X_FORWARDED_FOR)).thenReturn(X_FORWARDED_FOR);
        when(request.getRequestURI()).thenReturn("/should-log");

        when(request.getRequestURL()).thenReturn(urlStringBuffer);
        when(urlStringBuffer.toString()).thenReturn(URL);
        when(request.getQueryString()).thenThrow(UncheckedIOException.class);


        accessFilter.doFilter(request, response, filterChain);

        assertEquals(REFERER, RSD.get(Constants.REFERER));
        assertEquals(X_CLIENT, RSD.get(Constants.X_CLIENT));
        assertEquals(X_FORWARDED_FOR, RSD.get(Constants.X_FORWARDED_FOR));
        verify(request, times(1)).getHeader(Constants.REFERER);
        verify(request, times(1)).getHeader(Constants.X_CLIENT);
        verify(request, times(1)).getHeader(Constants.X_FORWARDED_FOR);
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getRequestURL();
        verify(request, times(1)).getQueryString();
        verifyNoInteractions(response);
        verify(filterChain, only()).doFilter(any(ContentCachingRequestWrapper.class),
                any(ContentCachingResponseWrapper.class));

        verify(MOCKED_APPENDER, atLeastOnce()).doAppend(loggingEventCaptor.capture());
        final var logEvents = loggingEventCaptor.getAllValues();
        assertEquals(3, logEvents.size());
        assertEquals("Error trying log initial request info. ", logEvents.get(0).getFormattedMessage());
        assertEquals("Request body: ", logEvents.get(1).getFormattedMessage());
        assertEquals("Response body: ", logEvents.get(2).getFormattedMessage());
    }

    @Test
    void doFilter_debugNotEnabled() throws IOException, ServletException {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(MOCKED_APPENDER);
        root.setLevel(Level.INFO);

        when(request.getHeader(Constants.REFERER)).thenReturn(REFERER);
        when(request.getHeader(Constants.X_CLIENT)).thenReturn(X_CLIENT);
        when(request.getHeader(Constants.X_FORWARDED_FOR)).thenReturn(X_FORWARDED_FOR);
        when(request.getRequestURI()).thenReturn("/should-log");
        when(request.getRequestURL()).thenReturn(urlStringBuffer);
        when(urlStringBuffer.toString()).thenReturn(URL);
        when(request.getQueryString()).thenReturn(QUERY_STRING);
        when(request.getMethod()).thenReturn(METHOD);
        when(request.getHeaderNames())
                .thenReturn(Iterators.asEnumeration(List.of(HEADER_KEY1, HEADER_KEY2).iterator()));
        when(request.getHeader(HEADER_KEY1)).thenReturn(HEADER_VALUE1);
        when(request.getHeader(HEADER_KEY2)).thenReturn(HEADER_VALUE2);

        accessFilter.doFilter(request, response, filterChain);

        assertEquals(REFERER, RSD.get(Constants.REFERER));
        assertEquals(X_CLIENT, RSD.get(Constants.X_CLIENT));
        assertEquals(X_FORWARDED_FOR, RSD.get(Constants.X_FORWARDED_FOR));
        verify(request, times(1)).getHeader(Constants.REFERER);
        verify(request, times(1)).getHeader(Constants.X_CLIENT);
        verify(request, times(1)).getHeader(Constants.X_FORWARDED_FOR);
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getRequestURL();
        verify(request, times(1)).getQueryString();
        verify(request, times(1)).getMethod();
        verify(request, times(1)).getHeaderNames();
        verify(request, times(1)).getHeader(HEADER_KEY1);
        verify(request, times(1)).getHeader(HEADER_KEY2);
        verifyNoInteractions(response);
        verify(filterChain, only()).doFilter(request, response);
        verify(MOCKED_APPENDER, atLeastOnce()).doAppend(loggingEventCaptor.capture());
        final var logEvents = loggingEventCaptor.getAllValues();
        assertEquals(1, logEvents.size());
        final var expectedHeadersLog = "Headers: %s=%s|%s=%s|".formatted(HEADER_KEY1, HEADER_VALUE1, HEADER_KEY2,
                HEADER_VALUE2);
        final var expectedLogLine = "Request -> %s -> %s %s -> Querystring: %s".formatted(METHOD,
                URL, expectedHeadersLog, QUERY_STRING);
        assertEquals(expectedLogLine, logEvents.getFirst().getFormattedMessage());
    }
}