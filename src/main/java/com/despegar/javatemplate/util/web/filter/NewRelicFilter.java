package com.despegar.javatemplate.util.web.filter;

import com.despegar.javatemplate.util.Constants;
import com.despegar.javatemplate.util.newrelic.NewRelicTransactionManager;
import com.despegar.library.routing.RSD;
import com.despegar.library.routing.uow.UowData;
import com.despegar.library.routing.uow.UowHelper;
import com.despegar.library.routing.version.VersionOverrideConstants;
import com.despegar.library.routing.version.VersionOverrideHelper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class NewRelicFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewRelicFilter.class);

    private final NewRelicTransactionManager transactionManager;
    private final List<String> ignoredPaths;

    public NewRelicFilter(NewRelicTransactionManager transactionManager, List<String> ignoredPaths) {
        this.transactionManager = transactionManager;
        this.ignoredPaths = ignoredPaths;
    }

    @Override
    public void doFilter(ServletRequest httpRequest, ServletResponse httpResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) httpRequest;

        if (traceableRequest(request)) {
            addBasicCustomParameters();
        } else {
            transactionManager.ignoreTransaction();
        }

        filterChain.doFilter(httpRequest, httpResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        LOGGER.debug("Initializing NewRelicFilter");
    }

    @Override
    public void destroy() {
        LOGGER.debug("Destroying NewRelicFilter");
    }

    private boolean traceableRequest(HttpServletRequest request) {
        return ignoredPaths.stream()
                .noneMatch(ignoredPath -> request.getRequestURI().toLowerCase().endsWith(ignoredPath));
    }

    private void addBasicCustomParameters() {
        UowData uowData = UowHelper.getCurrentUowData();

        transactionManager.addCustomParameter(UowHelper.KEY_UNIT_OF_WORK, getOrUnknown(uowData.getUow()));
        transactionManager.addCustomParameter(Constants.REQUEST_ID, getOrUnknown(uowData.getRequestId()));
        transactionManager.addCustomParameter(VersionOverrideConstants.X_VERSION_OVERRIDE,
                getOrUnknown(VersionOverrideHelper.getVersionOverride().getVersionOverride()));

        transactionManager.addCustomParameter(Constants.X_CLIENT, getFromContext(Constants.X_CLIENT));
        transactionManager.addCustomParameter(Constants.X_FORWARDED_FOR, getFromContext(Constants.X_FORWARDED_FOR));
    }

    private static String getFromContext(String contextKey) {
        final var valueFromContext = Optional.ofNullable(RSD.get(contextKey)).map(Object::toString).orElse("");
        return getOrUnknown(valueFromContext);
    }

    private static String getOrUnknown(String value) {
        return (value == null || value.isEmpty()) ? Constants.UNKNOWN : value;
    }
}
