package com.despegar.sofia_sales_test_tools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ConfigurationPropertiesScan("com.despegar.sofia_sales_test_tools")

// Local
@PropertySource(value = "classpath:environment.properties", encoding = "UTF-8", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:sensitive.conf", encoding = "UTF-8", ignoreResourceNotFound = true)

// Nebula contract
@PropertySource(value = "file:/service/config/environment.properties", encoding = "UTF-8", ignoreResourceNotFound = true)
@PropertySource(value = "file:/service/secrets/sensitive.conf", encoding = "UTF-8", ignoreResourceNotFound = true)

public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
