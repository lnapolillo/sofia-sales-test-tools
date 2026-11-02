package com.despegar.javatemplate.util.web.interceptor;

import com.despegar.javatemplate.util.Constants;
import com.despegar.javatemplate.util.newrelic.NewRelicTransactionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.Optional;

@Component
public class XServiceInterceptor implements HandlerInterceptor {

    private final NewRelicTransactionManager newRelicTransactionManager;

    @Autowired
    public XServiceInterceptor(NewRelicTransactionManager newRelicTransactionManager) {
        this.newRelicTransactionManager = newRelicTransactionManager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            final var method = handlerMethod.getMethod();
            final var serviceName = getServiceName(method);
            response.addHeader(Constants.X_SERVICE, serviceName);
            newRelicTransactionManager.setTransactionName(serviceName);
        }
        return true;
    }

    private String getServiceName(Method method) {
        return Optional.ofNullable(AnnotationUtils.findAnnotation(method, CustomServiceName.class)).map(CustomServiceName::value)
                .orElseGet(() -> String.format("%s.%s", method.getDeclaringClass().getSimpleName(), method.getName()));
    }
}
