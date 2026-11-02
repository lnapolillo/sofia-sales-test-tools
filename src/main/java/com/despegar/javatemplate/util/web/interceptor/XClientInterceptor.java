package com.despegar.javatemplate.util.web.interceptor;

import com.despegar.javatemplate.model.api.error.ApiHeaderMissingException;
import com.despegar.javatemplate.util.Constants;
import com.google.common.base.Strings;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class XClientInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(XClientInterceptor.class);

    private final boolean validationEnabled;
    private final List<String> validationIgnoredPaths;

    public XClientInterceptor(@Value("${xclient.validation.enabled}") Boolean validationEnabled,
    @Value("#{'${xclient.validation.ignored.paths}'}") List<String> validationIgnoredPaths) {
        this.validationEnabled = validationEnabled;
        this.validationIgnoredPaths = validationIgnoredPaths;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) {
        final var xClient = request.getHeader(Constants.X_CLIENT);

        if (Strings.isNullOrEmpty(xClient) && validationEnabled && notValidationIgnoredPath(request)) {
            final var errorMsg = "Header %s is required.".formatted(Constants.X_CLIENT);
            LOGGER.error(errorMsg);
            throw new ApiHeaderMissingException(errorMsg, Constants.X_CLIENT);
        }

        return true;
    }

    private boolean notValidationIgnoredPath(HttpServletRequest request) {
        return validationIgnoredPaths.stream()
                .noneMatch(ignoredPath -> request.getRequestURI().toLowerCase().contains(ignoredPath));
    }
}
