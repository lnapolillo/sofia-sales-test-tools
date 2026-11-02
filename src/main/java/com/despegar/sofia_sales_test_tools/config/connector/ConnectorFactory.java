package com.despegar.sofia_sales_test_tools.config.connector;

import com.despegar.sofia_sales_test_tools.config.connector.model.ClientResourceProperties;
import com.despegar.sofia_sales_test_tools.config.connector.model.ConnectorProperties;
import com.despegar.sofia_sales_test_tools.config.connector.model.JsonProperties;
import com.despegar.sofia_sales_test_tools.config.connector.model.RestConnectorProperties;
import com.despegar.sofia_sales_test_tools.exception.ConnectorNotInitialized;
import com.despegar.library.rest.RestConnector;
import com.despegar.library.rest.config.RestConnectorConfig;
import com.despegar.library.rest.interceptors.Interceptor;
import com.despegar.library.rest.interceptors.Interceptors;
import com.despegar.library.rest.interceptors.impl.AcceptJsonInterceptor;
import com.despegar.library.rest.interceptors.impl.CacheInterceptor;
import com.despegar.library.rest.interceptors.impl.RoutingInterceptor;
import com.despegar.library.rest.interceptors.impl.SnappyRequestBodyInterceptor;
import com.despegar.library.rest.interceptors.impl.SnappyResponseBodyInterceptor;
import com.despegar.library.rest.serializers.JsonSerializer;
import com.despegar.library.rest.serializers.JsonStreamSerializer;
import com.despegar.library.rest.serializers.Serializers;
import com.despegar.library.rest.serializers.StreamSerializers;
import com.despegar.library.rest.serializers.StringSerializer;
import com.despegar.library.rest.utils.DependencyUtils;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.despegar.sofia_sales_test_tools.config.connector.MapperFactory.createMapper;
import static java.util.Optional.ofNullable;

@Component
public class ConnectorFactory {
    public Connector create(
        String name,
        ConnectorProperties connectorProperties,
        List<Interceptor> customInterceptors)
    {
        Map<String, ClientResourceProperties> resourcesByName = ofNullable(connectorProperties.resources())
            .orElse(Map.of())
            .values()
            .stream()
            .collect(Collectors.toMap(ClientResourceProperties::name, Function.identity(), (first, second) -> second));

        RestConnector connector = create(name, connectorProperties.restConnector(), customInterceptors);

        return new Connector(name, connector, resourcesByName);
    }

    public Connector create(String name, ConnectorProperties connectorProperties) {
        return create(name, connectorProperties, Collections.emptyList());
    }

    private RestConnector create(
        String name,
        RestConnectorProperties properties,
        List<Interceptor> customInterceptors)
    {
        try {
            RestConnectorConfig connectorConfig = createRestConnectorConfig(properties);

            return new RestConnector(
                    properties.secure() ? "https" : "http",
                    properties.host(),
                    connectorConfig,
                    createSerializers(properties.json()),
                    createStreamSerializers(properties.json()),
                    createInterceptors(connectorConfig, properties, customInterceptors),
                    createSSLContext(name, properties));
        } catch (Exception e) {
            throw new ConnectorNotInitialized(name, e);
        }
    }

    private RestConnectorConfig createRestConnectorConfig(RestConnectorProperties properties) {
        RestConnectorConfig.ConfigBuilder connectorConfig = RestConnectorConfig
                .createBuilder()
                .endpoint(properties.endpoint())
                .setShutdownHookEnabled(properties.shutdownHookEnabled())
                .maxConnections(properties.maxConnections())
                .connectionTimeout(properties.connectionTimeout().toMillis())
                .readTimeout(properties.readTimeout().toMillis())
                .idleConnectionTimeout(properties.idleConnectionTimeout().toMillis())
                .requestMaxRetries(properties.request().maxRetries())
                .withValidateAfterInactivity(properties.validateAfterInactivity().toMillis());

        withCacheProperties(properties, connectorConfig);

        return connectorConfig.build();
    }

    private void withCacheProperties(
        RestConnectorProperties properties,
        RestConnectorConfig.ConfigBuilder connectorConfig)
    {
        if (properties.cache().enabled()) {
            connectorConfig.cacheSize(properties.cache().size());
            connectorConfig.cacheConcurrencyLevel(properties.cache().concurrencyLevel());

            EnumSet<CacheInterceptor.CacheMethods> supportedMethods = properties.cache()
                .supportedMethods()
                .stream()
                .map(supportedMethod -> CacheInterceptor.CacheMethods.valueOf(supportedMethod.toUpperCase()))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(CacheInterceptor.CacheMethods.class)));
            connectorConfig.supportedCacheMethods(supportedMethods);
        }
    }

    private SSLContext createSSLContext(String name, RestConnectorProperties properties) {
        return properties.secure() ? createSSLContext(name) : null;
    }

    private SSLContext createSSLContext(String name) {
        try {
            var context = SSLContext.getInstance("TLSv1.2");
            context.init(null, null, null);
            return context;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new ConnectorNotInitialized(name, e);
        }
    }

    private Serializers createSerializers(JsonProperties properties) {
        return Serializers.create(
                new JsonSerializer(createMapper(properties)),
                new StringSerializer());
    }

    private StreamSerializers createStreamSerializers(JsonProperties properties) {
        return StreamSerializers.create(new JsonStreamSerializer(createMapper(properties)));
    }

    private Interceptors createInterceptors(RestConnectorConfig config, RestConnectorProperties properties, List<Interceptor> customInterceptors) {
        Interceptors.Builder builder = Interceptors.Builder.create(config);

        builder.add(new AcceptJsonInterceptor());

        boolean snappyAvailable = DependencyUtils.isClassPresent("org.xerial.snappy.Snappy");
        if (snappyAvailable) {
            builder.add(new SnappyRequestBodyInterceptor());
            builder.add(new SnappyResponseBodyInterceptor());
        }

        if (config != null && config.isCacheEnabled()) {
            builder.add(new CacheInterceptor(config.getCacheSize(), config.getCacheConcurrencyLevel(), config.getSupportedCacheMethods()));
        }

        builder.add(new RoutingInterceptor(properties.clientId(), properties.xVersion(), false));
        builder.addAll(customInterceptors);
        return builder.build();
    }
}
