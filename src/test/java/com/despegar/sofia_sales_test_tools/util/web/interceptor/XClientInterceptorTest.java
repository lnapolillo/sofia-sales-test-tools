package com.despegar.sofia_sales_test_tools.util.web.interceptor;

import com.despegar.sofia_sales_test_tools.model.api.error.ApiHeaderMissingException;
import com.despegar.sofia_sales_test_tools.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.method.HandlerMethod;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class XClientInterceptorTest {

    private static final String CLIENT = "theClient";

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private HttpServletResponse responseMock;

    @Mock
    private HandlerMethod handlerMethodMock;

    @Test
    void preHandle_validationEnabledWithHeaderOk() {
        final var interceptor = new XClientInterceptor(true, List.of());

        when(requestMock.getHeader(Constants.X_CLIENT)).thenReturn(CLIENT);

        final var result = interceptor.preHandle(requestMock, responseMock, handlerMethodMock);

        assertTrue(result);
        verifyNoInteractions(responseMock);
        verifyNoInteractions(handlerMethodMock);
        verify(requestMock, only()).getHeader(Constants.X_CLIENT);
    }

    @Test
    void preHandle_validationEnabledWithMissingHeader() {
        final var interceptor = new XClientInterceptor(true, List.of());

        when(requestMock.getHeader(Constants.X_CLIENT)).thenReturn(null);

        assertThrows(ApiHeaderMissingException.class,
                () -> interceptor.preHandle(requestMock, responseMock, handlerMethodMock));

        verifyNoInteractions(responseMock);
        verifyNoInteractions(handlerMethodMock);
        verify(requestMock, only()).getHeader(Constants.X_CLIENT);
    }

    @Test
    void preHandle_validationDisabledWithHeaderOk() {
        final var interceptor = new XClientInterceptor(false, List.of());

        when(requestMock.getHeader(Constants.X_CLIENT)).thenReturn(CLIENT);

        final var result = interceptor.preHandle(requestMock, responseMock, handlerMethodMock);

        assertTrue(result);
        verifyNoInteractions(responseMock);
        verifyNoInteractions(handlerMethodMock);
        verify(requestMock, only()).getHeader(Constants.X_CLIENT);
    }

    @Test
    void preHandle_validationDisabledWithMissingHeader() {
        final var interceptor = new XClientInterceptor(false, List.of());

        when(requestMock.getHeader(Constants.X_CLIENT)).thenReturn(null);

        final var result = interceptor.preHandle(requestMock, responseMock, handlerMethodMock);

        assertTrue(result);
        verifyNoInteractions(responseMock);
        verifyNoInteractions(handlerMethodMock);
        verify(requestMock, only()).getHeader(Constants.X_CLIENT);
    }

    @Test
    void preHandle_validationEnabledMissingHeaderButPathIgnored() {
        final var path = "/path";
        final var interceptor = new XClientInterceptor(true, List.of(path, "/other"));

        when(requestMock.getHeader(Constants.X_CLIENT)).thenReturn(null);
        when(requestMock.getRequestURI()).thenReturn(path);

        final var result = interceptor.preHandle(requestMock, responseMock, handlerMethodMock);

        assertTrue(result);
        verifyNoInteractions(responseMock);
        verifyNoInteractions(handlerMethodMock);
        verify(requestMock, times(1)).getHeader(Constants.X_CLIENT);
        verify(requestMock, times(1)).getRequestURI();
        verifyNoMoreInteractions(responseMock);
    }
}
