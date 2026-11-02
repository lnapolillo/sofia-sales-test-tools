package com.despegar.javatemplate.controller.advice;

import com.despegar.javatemplate.model.api.error.ApiException;
import com.despegar.javatemplate.model.api.error.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenericControllerAdviceTest {

    private final GenericControllerAdvice advice = new GenericControllerAdvice();

    @Test
    void exception() {
        final var exception = mock(Exception.class);

        final var result = advice.exception(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals(ErrorResponse.unknown(), result.getBody());
    }

    @Test
    void apiException() {
        final var exception = mock(ApiException.class);
        final var httpStatus = mock(HttpStatus.class);
        final var errorResponse = mock(ErrorResponse.class);
        when(exception.getHttpStatus()).thenReturn(httpStatus);
        when(exception.getErrorResponse()).thenReturn(errorResponse);

        final var result = advice.exception(exception);

        assertEquals(httpStatus, result.getStatusCode());
        assertEquals(errorResponse, result.getBody());
    }

    @Test
    void missingParameterException() {
        final var exception = mock(MissingServletRequestParameterException.class);
        when(exception.getParameterName()).thenReturn("param");
        when(exception.getParameterType()).thenReturn("String");

        final var result = advice.exception(exception);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(ErrorResponse.missingRequiredParameter("param","String"), result.getBody());
    }
}
