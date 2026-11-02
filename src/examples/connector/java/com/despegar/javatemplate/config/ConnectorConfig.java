package com.despegar.javatemplate.config;

import com.despegar.javatemplate.config.connector.Connector;
import com.despegar.javatemplate.config.connector.ConnectorFactory;
import com.despegar.javatemplate.config.connector.model.ClientResourceProperties;
import com.despegar.javatemplate.config.connector.model.ConnectorProperties;
import com.despegar.javatemplate.config.connector.model.RestConnectorProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Configuration
@PropertySource(value = "classpath:connector.yaml", encoding = "UTF-8", factory = YamlPropertySourceFactory.class)
public class ConnectorConfig {

    @Bean("geoConnector")
    public Connector geoConnector(ConnectorFactory factory, GeoConnectorProperties properties) {
        return factory.create("geo", properties);
    }

    @ConfigurationProperties(prefix = "geo")
    public static class GeoConnectorProperties extends ConnectorProperties {
        public GeoConnectorProperties(
            RestConnectorProperties restConnector,
            Map<String, ClientResourceProperties> resources)
        {
            super(restConnector, resources);
        }
    }
}
