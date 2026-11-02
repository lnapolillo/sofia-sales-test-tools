package com.despegar.sofia_sales_test_tools.config.connector;

import com.despegar.sofia_sales_test_tools.config.connector.model.*;
import com.despegar.library.rest.interceptors.Interceptor;
import com.despegar.library.rest.interceptors.Interceptors;
import com.despegar.library.rest.interceptors.impl.CacheInterceptor;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class ConnectorFactoryTest {
    private final ConnectorFactory target = new ConnectorFactory();
    private final JsonProperties jsonProperties = JsonProperties.of(JsonFormat.SNAKE_CASE);

    @Mock
    private RestConnectorProperties restConnectorPropertiesMock;
    @Mock
    private RequestProperties requestPropertiesMock;
    @Mock
    private CacheProperties cachePropertiesMock;
    @Mock
    private ClientResourceProperties clientResourcePropertiesMock;



    @Test
    void createTest_ok(){
        Map<String, ClientResourceProperties> resources =
            Collections.singletonMap("exampleTag", clientResourcePropertiesMock);
        MockConnectorProperties connectorProperties =
            new MockConnectorProperties(restConnectorPropertiesMock, resources);

        when(clientResourcePropertiesMock.name()).thenReturn("name");
        when(restConnectorPropertiesMock.endpoint()).thenReturn("endpoint-app");
        when(restConnectorPropertiesMock.shutdownHookEnabled()).thenReturn(true);
        when(restConnectorPropertiesMock.maxConnections()).thenReturn(20);
        when(restConnectorPropertiesMock.connectionTimeout()).thenReturn(Duration.of(200, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.readTimeout()).thenReturn(Duration.of(300, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.idleConnectionTimeout()).thenReturn(Duration.of(400, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.request()).thenReturn(requestPropertiesMock);
        when(requestPropertiesMock.maxRetries()).thenReturn(7);
        when(restConnectorPropertiesMock.validateAfterInactivity()).thenReturn(Duration.of(33, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.cache()).thenReturn(cachePropertiesMock);
        when(cachePropertiesMock.enabled()).thenReturn(true);
        when(cachePropertiesMock.size()).thenReturn(10);
        when(cachePropertiesMock.concurrencyLevel()).thenReturn(20);
        when(cachePropertiesMock.supportedMethods())
            .thenReturn(Lists.newArrayList(CacheInterceptor.CacheMethods.MAX_AGE.name()));
        when(restConnectorPropertiesMock.secure()).thenReturn(true);
        when(restConnectorPropertiesMock.host()).thenReturn("host");
        when(restConnectorPropertiesMock.json()).thenReturn(jsonProperties);
        when(restConnectorPropertiesMock.clientId()).thenReturn("clientId");
        when(restConnectorPropertiesMock.xVersion()).thenReturn("xVersion");

        Connector connector = this.target.create("geo", connectorProperties);

        assertEquals("https://host", connector.restConnector().getEndpoint());
        assertEquals("geo", connector.name());
        assertEquals(clientResourcePropertiesMock, connector.resourcesByName().get("name"));
        assertNotNull(connector.restConnector().getSerializers());
        assertNotNull(connector.restConnector().getStreamSerializers());
        assertEquals(5, connector.restConnector().getInterceptors().size());

        verify(restConnectorPropertiesMock, times(2)).secure();
        verify(restConnectorPropertiesMock).host();
        verify(restConnectorPropertiesMock).endpoint();
        verify(restConnectorPropertiesMock).shutdownHookEnabled();
        verify(restConnectorPropertiesMock).maxConnections();
        verify(restConnectorPropertiesMock).connectionTimeout();
        verify(restConnectorPropertiesMock).request();
        verify(requestPropertiesMock).maxRetries();
        verify(restConnectorPropertiesMock).validateAfterInactivity();
        verify(restConnectorPropertiesMock, times(4)).cache();
        verify(cachePropertiesMock).enabled();
        verify(cachePropertiesMock).concurrencyLevel();
        verify(cachePropertiesMock).size();
        verify(cachePropertiesMock).supportedMethods();
        verify(restConnectorPropertiesMock, times(2)).json();
    }

    @Test
    void createWithInterceptorTest(){
        Interceptor interceptor = Mockito.mock(Interceptor.class);

        Map<String, ClientResourceProperties> resources =
            Collections.singletonMap("exampleTag", clientResourcePropertiesMock);
        MockConnectorProperties connectorProperties =
            new MockConnectorProperties(restConnectorPropertiesMock, resources);

        when(clientResourcePropertiesMock.name()).thenReturn("name");
        when(restConnectorPropertiesMock.endpoint()).thenReturn("endpoint-app");
        when(restConnectorPropertiesMock.shutdownHookEnabled()).thenReturn(true);
        when(restConnectorPropertiesMock.maxConnections()).thenReturn(20);
        when(restConnectorPropertiesMock.connectionTimeout()).thenReturn(Duration.of(200, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.readTimeout()).thenReturn(Duration.of(300, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.idleConnectionTimeout()).thenReturn(Duration.of(400, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.request()).thenReturn(requestPropertiesMock);
        when(requestPropertiesMock.maxRetries()).thenReturn(7);
        when(restConnectorPropertiesMock.validateAfterInactivity()).thenReturn(Duration.of(33, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.cache()).thenReturn(cachePropertiesMock);
        when(cachePropertiesMock.enabled()).thenReturn(true);
        when(cachePropertiesMock.size()).thenReturn(10);
        when(cachePropertiesMock.concurrencyLevel()).thenReturn(20);
        when(cachePropertiesMock.supportedMethods())
            .thenReturn(Lists.newArrayList(CacheInterceptor.CacheMethods.MAX_AGE.name()));
        when(restConnectorPropertiesMock.secure()).thenReturn(true);
        when(restConnectorPropertiesMock.host()).thenReturn("host");
        when(restConnectorPropertiesMock.json()).thenReturn(jsonProperties);
        when(restConnectorPropertiesMock.clientId()).thenReturn("clientId");
        when(restConnectorPropertiesMock.xVersion()).thenReturn("xVersion");

        Connector connector = this.target.create("geo", connectorProperties, List.of(interceptor));

        assertEquals("https://host", connector.restConnector().getEndpoint());
        assertEquals("geo", connector.name());
        assertEquals(clientResourcePropertiesMock, connector.resourcesByName().get("name"));
        assertNotNull(connector.restConnector().getSerializers());
        assertNotNull(connector.restConnector().getStreamSerializers());
        assertEquals(6, connector.restConnector().getInterceptors().size());
        Interceptor lastInterceptor = getLastInterceptor(connector.restConnector().getInterceptors());
        assertSame(lastInterceptor, interceptor);

        verify(restConnectorPropertiesMock, times(2)).secure();
        verify(restConnectorPropertiesMock).host();
        verify(restConnectorPropertiesMock).endpoint();
        verify(restConnectorPropertiesMock).shutdownHookEnabled();
        verify(restConnectorPropertiesMock).maxConnections();
        verify(restConnectorPropertiesMock).connectionTimeout();
        verify(restConnectorPropertiesMock).request();
        verify(requestPropertiesMock).maxRetries();
        verify(restConnectorPropertiesMock).validateAfterInactivity();
        verify(restConnectorPropertiesMock, times(4)).cache();
        verify(cachePropertiesMock).enabled();
        verify(cachePropertiesMock).concurrencyLevel();
        verify(cachePropertiesMock).size();
        verify(cachePropertiesMock).supportedMethods();
        verify(restConnectorPropertiesMock, times(2)).json();
    }

    @Test
    void createTest_cacheOff_ok(){
        Map<String, ClientResourceProperties> resources =
            Collections.singletonMap("exampleTag", clientResourcePropertiesMock);
        MockConnectorProperties connectorProperties =
            new MockConnectorProperties(restConnectorPropertiesMock, resources);

        when(clientResourcePropertiesMock.name()).thenReturn("name");
        when(restConnectorPropertiesMock.endpoint()).thenReturn("endpoint-app");
        when(restConnectorPropertiesMock.shutdownHookEnabled()).thenReturn(true);
        when(restConnectorPropertiesMock.maxConnections()).thenReturn(20);
        when(restConnectorPropertiesMock.connectionTimeout()).thenReturn(Duration.of(200, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.readTimeout()).thenReturn(Duration.of(300, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.idleConnectionTimeout()).thenReturn(Duration.of(400, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.request()).thenReturn(requestPropertiesMock);
        when(requestPropertiesMock.maxRetries()).thenReturn(7);
        when(restConnectorPropertiesMock.validateAfterInactivity()).thenReturn(Duration.of(33, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.cache()).thenReturn(cachePropertiesMock);
        when(cachePropertiesMock.enabled()).thenReturn(false);
        when(restConnectorPropertiesMock.secure()).thenReturn(true);
        when(restConnectorPropertiesMock.host()).thenReturn("host");
        when(restConnectorPropertiesMock.json()).thenReturn(jsonProperties);
        when(restConnectorPropertiesMock.clientId()).thenReturn("clientId");
        when(restConnectorPropertiesMock.xVersion()).thenReturn("xVersion");

        Connector connector = this.target.create("geo", connectorProperties);

        assertEquals("https://host", connector.restConnector().getEndpoint());
        assertEquals("geo", connector.name());
        assertEquals(clientResourcePropertiesMock, connector.resourcesByName().get("name"));
        assertNotNull(connector.restConnector().getSerializers());
        assertNotNull(connector.restConnector().getStreamSerializers());
        assertEquals(4, connector.restConnector().getInterceptors().size());

        verify(restConnectorPropertiesMock, times(2)).secure();
        verify(restConnectorPropertiesMock).host();
        verify(restConnectorPropertiesMock).endpoint();
        verify(restConnectorPropertiesMock).shutdownHookEnabled();
        verify(restConnectorPropertiesMock).maxConnections();
        verify(restConnectorPropertiesMock).connectionTimeout();
        verify(restConnectorPropertiesMock).request();
        verify(requestPropertiesMock).maxRetries();
        verify(restConnectorPropertiesMock).validateAfterInactivity();
        verify(restConnectorPropertiesMock).cache();
        verify(cachePropertiesMock).enabled();
        verify(cachePropertiesMock, times(0)).concurrencyLevel();
        verify(cachePropertiesMock, times(0)).size();
        verify(cachePropertiesMock, times(0)).supportedMethods();
        verify(restConnectorPropertiesMock, times(2)).json();
    }

    @Test
    void createTest_notSecure_ok(){
        Map<String, ClientResourceProperties> resources =
            Collections.singletonMap("exampleTag", clientResourcePropertiesMock);
        MockConnectorProperties connectorProperties =
            new MockConnectorProperties(restConnectorPropertiesMock, resources);

        when(clientResourcePropertiesMock.name()).thenReturn("name");
        when(restConnectorPropertiesMock.endpoint()).thenReturn("endpoint-app");
        when(restConnectorPropertiesMock.shutdownHookEnabled()).thenReturn(true);
        when(restConnectorPropertiesMock.maxConnections()).thenReturn(20);
        when(restConnectorPropertiesMock.connectionTimeout()).thenReturn(Duration.of(200, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.readTimeout()).thenReturn(Duration.of(300, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.idleConnectionTimeout()).thenReturn(Duration.of(400, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.request()).thenReturn(requestPropertiesMock);
        when(requestPropertiesMock.maxRetries()).thenReturn(7);
        when(restConnectorPropertiesMock.validateAfterInactivity()).thenReturn(Duration.of(33, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.cache()).thenReturn(cachePropertiesMock);
        when(cachePropertiesMock.enabled()).thenReturn(true);
        when(cachePropertiesMock.size()).thenReturn(10);
        when(cachePropertiesMock.concurrencyLevel()).thenReturn(20);
        when(cachePropertiesMock.supportedMethods())
            .thenReturn(Lists.newArrayList(CacheInterceptor.CacheMethods.MAX_AGE.name()));
        when(restConnectorPropertiesMock.secure()).thenReturn(false);
        when(restConnectorPropertiesMock.host()).thenReturn("host");
        when(restConnectorPropertiesMock.json()).thenReturn(jsonProperties);
        when(restConnectorPropertiesMock.clientId()).thenReturn("clientId");
        when(restConnectorPropertiesMock.xVersion()).thenReturn("xVersion");

        var connector = this.target.create("geo", connectorProperties);

        assertEquals("http://host", connector.restConnector().getEndpoint());
        assertEquals("geo", connector.name());
        assertEquals(clientResourcePropertiesMock, connector.resourcesByName().get("name"));
        assertNotNull(connector.restConnector().getSerializers());
        assertNotNull(connector.restConnector().getStreamSerializers());
        assertEquals(5, connector.restConnector().getInterceptors().size());

        verify(restConnectorPropertiesMock, times(2)).secure();
        verify(restConnectorPropertiesMock).host();
        verify(restConnectorPropertiesMock).endpoint();
        verify(restConnectorPropertiesMock).shutdownHookEnabled();
        verify(restConnectorPropertiesMock).maxConnections();
        verify(restConnectorPropertiesMock).connectionTimeout();
        verify(restConnectorPropertiesMock).request();
        verify(requestPropertiesMock).maxRetries();
        verify(restConnectorPropertiesMock).validateAfterInactivity();
        verify(restConnectorPropertiesMock, times(4)).cache();
        verify(cachePropertiesMock).enabled();
        verify(cachePropertiesMock).concurrencyLevel();
        verify(cachePropertiesMock).size();
        verify(cachePropertiesMock).supportedMethods();
        verify(restConnectorPropertiesMock, times(2)).json();
    }

    @Test
    void createTest_shutdownHookNotEnabled_ok(){
        Map<String, ClientResourceProperties> resources =
            Collections.singletonMap("exampleTag", clientResourcePropertiesMock);
        MockConnectorProperties connectorProperties =
            new MockConnectorProperties(restConnectorPropertiesMock, resources);

        when(clientResourcePropertiesMock.name()).thenReturn("name");
        when(restConnectorPropertiesMock.endpoint()).thenReturn("endpoint-app");
        when(restConnectorPropertiesMock.shutdownHookEnabled()).thenReturn(false);
        when(restConnectorPropertiesMock.maxConnections()).thenReturn(20);
        when(restConnectorPropertiesMock.connectionTimeout()).thenReturn(Duration.of(200, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.readTimeout()).thenReturn(Duration.of(300, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.idleConnectionTimeout()).thenReturn(Duration.of(400, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.request()).thenReturn(requestPropertiesMock);
        when(requestPropertiesMock.maxRetries()).thenReturn(7);
        when(restConnectorPropertiesMock.validateAfterInactivity()).thenReturn(Duration.of(33, ChronoUnit.SECONDS));
        when(restConnectorPropertiesMock.cache()).thenReturn(cachePropertiesMock);
        when(cachePropertiesMock.enabled()).thenReturn(true);
        when(cachePropertiesMock.size()).thenReturn(10);
        when(cachePropertiesMock.concurrencyLevel()).thenReturn(20);
        when(cachePropertiesMock.supportedMethods())
            .thenReturn(Lists.newArrayList(CacheInterceptor.CacheMethods.MAX_AGE.name()));
        when(restConnectorPropertiesMock.secure()).thenReturn(true);
        when(restConnectorPropertiesMock.host()).thenReturn("host");
        when(restConnectorPropertiesMock.json()).thenReturn(jsonProperties);
        when(restConnectorPropertiesMock.clientId()).thenReturn("clientId");
        when(restConnectorPropertiesMock.xVersion()).thenReturn("xVersion");

        var connector = this.target.create("geo", connectorProperties);

        assertEquals("https://host", connector.restConnector().getEndpoint());
        assertEquals("geo", connector.name());
        assertEquals(clientResourcePropertiesMock, connector.resourcesByName().get("name"));
        assertNotNull(connector.restConnector().getSerializers());
        assertNotNull(connector.restConnector().getStreamSerializers());
        assertEquals(5, connector.restConnector().getInterceptors().size());

        verify(restConnectorPropertiesMock, times(2)).secure();
        verify(restConnectorPropertiesMock).host();
        verify(restConnectorPropertiesMock).endpoint();
        verify(restConnectorPropertiesMock).shutdownHookEnabled();
        verify(restConnectorPropertiesMock).maxConnections();
        verify(restConnectorPropertiesMock).connectionTimeout();
        verify(restConnectorPropertiesMock).request();
        verify(requestPropertiesMock).maxRetries();
        verify(restConnectorPropertiesMock).validateAfterInactivity();
        verify(restConnectorPropertiesMock, times(4)).cache();
        verify(cachePropertiesMock).enabled();
        verify(cachePropertiesMock).concurrencyLevel();
        verify(cachePropertiesMock).size();
        verify(cachePropertiesMock).supportedMethods();
        verify(restConnectorPropertiesMock, times(2)).json();
    }

    private static class MockConnectorProperties extends ConnectorProperties {
        public MockConnectorProperties(
            RestConnectorProperties connector,
            Map<String, ClientResourceProperties> resources)
        {
            super(connector, resources);
        }
    }

    private <T> T getLastInterceptor(Interceptors interceptors) {
        try {
            Field field = interceptors.getClass().getDeclaredField("lastInterceptor");
            field.setAccessible(true);
            @SuppressWarnings("unchecked") T value = (T) field.get(interceptors);
            return value;
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            return null;
        }
    }
}
