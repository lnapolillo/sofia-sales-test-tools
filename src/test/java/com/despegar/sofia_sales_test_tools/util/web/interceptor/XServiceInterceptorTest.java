package com.despegar.sofia_sales_test_tools.util.web.interceptor;

import com.despegar.sofia_sales_test_tools.util.Constants;
import com.despegar.sofia_sales_test_tools.util.newrelic.NewRelicTransactionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.method.HandlerMethod;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class XServiceInterceptorTest {

    private static final String CUSTOM_SERVICE_NAME = "serviceName";

    @Mock
    private HttpServletRequest httpRequestMock;

    @Mock
    private HttpServletResponse httpResponseMock;

    @Mock
    private HandlerMethod handlerMethodMock;

    @Mock
    private NewRelicTransactionManager newRelicTransactionManager;

    @InjectMocks
    private XServiceInterceptor interceptor;


    @Test
    void withCustomServiceNameAnnotation() throws NoSuchMethodException {
        final var methodWithServiceNameAnnotation = this.getClass().getMethod("methodWithCustomServiceName");
        when(handlerMethodMock.getMethod()).thenReturn(methodWithServiceNameAnnotation);

        final var result = interceptor.preHandle(httpRequestMock, httpResponseMock, handlerMethodMock);

        assertTrue(result);
        verifyNoInteractions(httpRequestMock);
        verify(handlerMethodMock, only()).getMethod();
        verify(httpResponseMock, only()).addHeader(Constants.X_SERVICE, CUSTOM_SERVICE_NAME);
        verify(newRelicTransactionManager, only()).setTransactionName(CUSTOM_SERVICE_NAME);
    }

    @Test
    void withoutCustomServiceNameAnnotation() throws NoSuchMethodException {
        final var methodWithoutCustomServiceName = "methodWithoutCustomServiceName";
        final var methodWithoutServiceNameAnnotation = this.getClass().getMethod(methodWithoutCustomServiceName);
        when(handlerMethodMock.getMethod()).thenReturn(methodWithoutServiceNameAnnotation);
        final var expectedServiceName = "%s.%s".formatted(getClass().getSimpleName(), methodWithoutCustomServiceName);

        final var result = interceptor.preHandle(httpRequestMock, httpResponseMock, handlerMethodMock);

        Assertions.assertTrue(result);
        verifyNoInteractions(httpRequestMock);
        verify(handlerMethodMock, only()).getMethod();
        verify(httpResponseMock, only()).addHeader(Constants.X_SERVICE, expectedServiceName);
        verify(newRelicTransactionManager, only()).setTransactionName(expectedServiceName);
    }

    @CustomServiceName(CUSTOM_SERVICE_NAME)
    public void methodWithCustomServiceName() {
    }

    public void methodWithoutCustomServiceName() {
        //do not delete this method! it is used to test the behavior
    }

    @Test
    void notAHandler() {
        final var result = interceptor.preHandle(httpRequestMock, httpResponseMock, "not a handler");

        assertTrue(result);
        verifyNoInteractions(httpRequestMock, handlerMethodMock, newRelicTransactionManager);

    }

}
