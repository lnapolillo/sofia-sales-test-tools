package com.despegar.javatemplate.config;

import com.despegar.javatemplate.util.web.interceptor.XClientInterceptor;
import com.despegar.javatemplate.util.web.interceptor.XServiceInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    private final List<String> staticPathsToExclude;
    private final XClientInterceptor xClientInterceptor;
    private final XServiceInterceptor xServiceInterceptor;

    public InterceptorConfiguration(@Value("#{'${routing.static.paths.to.exclude}'}") List<String> staticPathsToExclude,
                                    XClientInterceptor xClientInterceptor, XServiceInterceptor xServiceInterceptor) {
        this.staticPathsToExclude = staticPathsToExclude;
        this.xClientInterceptor = xClientInterceptor;
        this.xServiceInterceptor = xServiceInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration xClientRegistration = registry.addInterceptor(xClientInterceptor);
        xClientRegistration.excludePathPatterns(staticPathsToExclude);

        InterceptorRegistration xServiceRegistration = registry.addInterceptor(xServiceInterceptor);
        xServiceRegistration.excludePathPatterns(staticPathsToExclude);
    }

}