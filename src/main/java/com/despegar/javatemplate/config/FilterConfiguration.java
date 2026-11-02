package com.despegar.javatemplate.config;

import com.despegar.javatemplate.config.compression.CompressionFilter;
import com.despegar.javatemplate.util.newrelic.NewRelicTransactionManager;
import com.despegar.javatemplate.util.web.filter.AccessFilter;
import com.despegar.javatemplate.util.web.filter.NewRelicFilter;
import com.despegar.library.routing.filter.RoutingFilter;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FilterConfiguration {

    private static final String ALL_URL = "/*";
    private static final int ROUTING_FILTER_ORDER = 1;
    private static final int COMPRESSION_FILTER_ORDER = 2;
    private static final int ACCESS_FILTER_ORDER = 3;
    private static final int NEW_RELIC_FILTER_ORDER = 4;

    @Bean
    public FilterRegistrationBean<Filter> routingFilter(@Value("${project.artifactId}") String artifactId,
                                                        @Value("${app.version}") String version) {
        return initializeFilterRegistrationBean(new RoutingFilter(artifactId + "-v" + version), ROUTING_FILTER_ORDER, ALL_URL);
    }

    @Bean
    public FilterRegistrationBean<Filter> accessFilter(
            @Value("#{'${routing.static.paths.to.exclude}'}") List<String> ignoredPaths) {
        return initializeFilterRegistrationBean(new AccessFilter(ignoredPaths), ACCESS_FILTER_ORDER, ALL_URL);
    }

    @Bean
    public FilterRegistrationBean<Filter> newRelicFilter(NewRelicTransactionManager newRelicTransactionManager,
                                                         @Value("#{'${routing.static.paths.to.exclude}'}") List<String> ignoredPaths) {
        return initializeFilterRegistrationBean(
                new NewRelicFilter(newRelicTransactionManager, ignoredPaths), NEW_RELIC_FILTER_ORDER, ALL_URL);
    }

    @Bean
    public FilterRegistrationBean<Filter> compressionFilter() {
        return initializeFilterRegistrationBean(new CompressionFilter(), COMPRESSION_FILTER_ORDER, ALL_URL);
    }

    private <T extends Filter> FilterRegistrationBean<Filter> initializeFilterRegistrationBean(T filter, int order, String... urlPatterns) {
        final var registrationBean = new FilterRegistrationBean<>();
        registrationBean.setOrder(order);
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(urlPatterns);
        return registrationBean;
    }
}
