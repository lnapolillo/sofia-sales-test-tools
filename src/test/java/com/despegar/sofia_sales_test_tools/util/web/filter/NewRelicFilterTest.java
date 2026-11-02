package com.despegar.sofia_sales_test_tools.util.web.filter;

import com.despegar.sofia_sales_test_tools.util.Constants;
import com.despegar.sofia_sales_test_tools.util.newrelic.NewRelicTransactionManager;
import com.despegar.library.routing.RSD;
import com.despegar.library.routing.uow.UowHelper;
import com.despegar.library.routing.version.VersionOverrideConstants;
import com.despegar.library.routing.version.VersionOverrideHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class NewRelicFilterTest {

    private static final String IGNORED_PATH = "/ignored";
    private static final String SOME_PATH = "/some-path";

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private NewRelicTransactionManager newRelicTransactionManager;

    private NewRelicFilter newRelicFilter;

    @BeforeEach
    void init() {
        RSD.clear();
        newRelicFilter = new NewRelicFilter(newRelicTransactionManager, List.of(IGNORED_PATH));
        newRelicFilter.init(null);
    }

    @AfterEach
    void destroy(){
        newRelicFilter.destroy();
    }


    @Test
    void doFilter_nonTraceableRequest() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn(IGNORED_PATH);

        newRelicFilter.doFilter(request, response, filterChain);

        verify(request, times(1)).getRequestURI();
        verify(newRelicTransactionManager, only()).ignoreTransaction();
        verifyNoInteractions(response);
        verify(filterChain, only()).doFilter(request, response);
    }

    @Test
    void doFilter_traceableRequest() throws ServletException, IOException {
        final var client = "client";
        final var forwardedFor = "forwardedFor";
        final var uow = "uow";
        final var requestId = "rqId";
        final var versionOverride = "vOverride";

        when(request.getRequestURI()).thenReturn(SOME_PATH);

        RSD.put(Constants.X_CLIENT, client);
        RSD.put(Constants.X_FORWARDED_FOR, forwardedFor);
        UowHelper.setExistingUnitOfWork(uow, requestId);
        VersionOverrideHelper.set(versionOverride);

        newRelicFilter.doFilter(request, response, filterChain);

        verify(request, times(1)).getRequestURI();
        verify(newRelicTransactionManager, times(1)).addCustomParameter(UowHelper.KEY_UNIT_OF_WORK, uow);
        verify(newRelicTransactionManager, times(1)).addCustomParameter(Constants.REQUEST_ID, requestId);
        verify(newRelicTransactionManager, times(1)).addCustomParameter(VersionOverrideConstants.X_VERSION_OVERRIDE, versionOverride);
        verify(newRelicTransactionManager, times(1)).addCustomParameter(Constants.X_CLIENT, client);
        verify(newRelicTransactionManager, times(1)).addCustomParameter(Constants.X_FORWARDED_FOR, forwardedFor);
        verifyNoMoreInteractions(newRelicTransactionManager);
        verifyNoInteractions(response);
        verify(filterChain, only()).doFilter(request, response);
    }

    @Test
    void doFilter_traceableRequest_defaults() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn(SOME_PATH);

        newRelicFilter.doFilter(request, response, filterChain);

        verify(request, times(1)).getRequestURI();
        verify(newRelicTransactionManager, times(1)).addCustomParameter(UowHelper.KEY_UNIT_OF_WORK, Constants.UNKNOWN);
        verify(newRelicTransactionManager, times(1)).addCustomParameter(Constants.REQUEST_ID, Constants.UNKNOWN);
        verify(newRelicTransactionManager, times(1)).addCustomParameter(VersionOverrideConstants.X_VERSION_OVERRIDE, Constants.UNKNOWN);
        verify(newRelicTransactionManager, times(1)).addCustomParameter(Constants.X_CLIENT, Constants.UNKNOWN);
        verify(newRelicTransactionManager, times(1)).addCustomParameter(Constants.X_FORWARDED_FOR, Constants.UNKNOWN);
        verifyNoMoreInteractions(newRelicTransactionManager);
        verifyNoInteractions(response);
        verify(filterChain, only()).doFilter(request, response);
    }

}
