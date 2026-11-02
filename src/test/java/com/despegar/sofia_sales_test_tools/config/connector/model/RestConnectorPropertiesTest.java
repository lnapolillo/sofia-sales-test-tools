package com.despegar.sofia_sales_test_tools.config.connector.model;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static com.despegar.library.rest.interceptors.impl.CacheInterceptor.CacheMethods.*;

class RestConnectorPropertiesTest {

    @Test
    void RestConnectorPropertiesDefaultValuesTest_ok(){
        var result = new RestConnectorProperties(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Assertions.assertNull(result.host());
        Assertions.assertNull(result.protocol());
        Assertions.assertNull(result.endpoint());
        Assertions.assertEquals(false,result.secure());
        Assertions.assertEquals(false,result.shutdownHookEnabled());
        Assertions.assertEquals(20,result.maxConnections());
        Assertions.assertEquals(Duration.ofMillis(500),result.connectionTimeout());
        Assertions.assertEquals(Duration.of(30, ChronoUnit.SECONDS),result.readTimeout());
        Assertions.assertEquals(Duration.of(60, ChronoUnit.SECONDS),result.idleConnectionTimeout());
        Assertions.assertEquals(false,result.cache().enabled());
        Assertions.assertEquals(50,result.cache().concurrencyLevel());
        Assertions.assertEquals(1000,result.cache().size());
        Assertions.assertEquals(Lists.newArrayList(MAX_AGE.name(),EXPIRES.name(),ETAG.name(),LAST_MODIFIED.name()),result.cache().supportedMethods());
        Assertions.assertEquals(JsonProperties.of(JsonFormat.SNAKE_CASE),result.json());
        Assertions.assertEquals(Duration.ofMillis(-1),result.validateAfterInactivity());
        Assertions.assertEquals(new RequestProperties(1),result.request());
        Assertions.assertNull(result.xVersion());
        Assertions.assertNull(result.clientId());
    }
}
