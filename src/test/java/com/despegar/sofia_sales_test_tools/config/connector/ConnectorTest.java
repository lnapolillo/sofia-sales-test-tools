package com.despegar.sofia_sales_test_tools.config.connector;

import com.despegar.sofia_sales_test_tools.config.connector.model.ClientResourceProperties;
import com.despegar.sofia_sales_test_tools.exception.ConnectorException;
import com.despegar.library.rest.HttpMethod;
import com.despegar.library.rest.HttpResponse;
import com.despegar.library.rest.HttpStatus;
import com.despegar.library.rest.RestConnector;
import com.despegar.library.rest.builder.HttpRequestBuilder;
import com.despegar.library.rest.compression.ContentEncoding;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class ConnectorTest {
    @Mock
    private RestConnector restConnectorMock;
    @Mock
    private HttpRequestBuilder httpRequestBuilderMock;
    @Mock
    private HttpResponse httpResponseMock;
    @Mock
    private ClientResourceProperties clientResourcePropertiesMock;


    @Test
    void getExecuteTest_ok() throws IOException {
        String body = "{'jsonBody':'valueExample'}";

        when(restConnectorMock.get(anyString())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.acceptEncoding(any(ContentEncoding.class))).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.encodeWith(any(ContentEncoding.class))).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.accept(anyString())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.asContentType(anyString())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.withBody(any())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.withHeaders(any())).thenReturn(httpRequestBuilderMock);
        when(httpResponseMock.getStatus()).thenReturn(HttpStatus.OK);
        when(httpRequestBuilderMock.execute()).thenReturn(httpResponseMock);

        Connector connector = buildConnector(null, "path");
        Connector.RequestBuilder requestBuilder = connector.ofResource("exampleTag");
        requestBuilder.body(body);
        HttpResponse httpResponse = requestBuilder.execute();

        assertEquals(httpResponseMock, httpResponse);

        verify(httpRequestBuilderMock).acceptEncoding(ContentEncoding.GZIP);
        verify(httpRequestBuilderMock).encodeWith(ContentEncoding.GZIP);
        verify(httpRequestBuilderMock).accept("application/json");
        verify(httpRequestBuilderMock).asContentType("application/json");
        verify(httpRequestBuilderMock).withBody(body);
        verify(httpRequestBuilderMock).withHeaders(any());
        verify(httpRequestBuilderMock, never()).withResponseTimeout(anyLong());
        verify(httpRequestBuilderMock).execute();
    }

    @Test
    void getExecuteTest_withSpecifiedResponseTimeout_ok() throws IOException {
        String body = "{'jsonBody':'valueExample'}";

        when(restConnectorMock.get(anyString())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.acceptEncoding(any(ContentEncoding.class))).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.encodeWith(any(ContentEncoding.class))).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.accept(anyString())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.asContentType(anyString())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.withBody(any())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.withHeaders(any())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.withResponseTimeout(Duration.ofSeconds(3).toMillis()))
            .thenReturn(httpRequestBuilderMock);
        when(httpResponseMock.getStatus()).thenReturn(HttpStatus.OK);
        when(httpRequestBuilderMock.execute()).thenReturn(httpResponseMock);

        Connector connector = buildConnector(Duration.ofSeconds(3), "path");
        Connector.RequestBuilder requestBuilder = connector.ofResource("exampleTag");
        requestBuilder.body(body);
        HttpResponse httpResponse = requestBuilder.execute();

        assertEquals(httpResponseMock, httpResponse);

        verify(httpRequestBuilderMock).acceptEncoding(ContentEncoding.GZIP);
        verify(httpRequestBuilderMock).encodeWith(ContentEncoding.GZIP);
        verify(httpRequestBuilderMock).accept("application/json");
        verify(httpRequestBuilderMock).asContentType("application/json");
        verify(httpRequestBuilderMock).withBody(body);
        verify(httpRequestBuilderMock).withHeaders(any());
        verify(httpRequestBuilderMock).withResponseTimeout(3000L); // 3 seconds in milliseconds
        verify(httpRequestBuilderMock).execute();
    }

    @Test
    void getExecuteTest_withPathVariable_ok() throws IOException {
        String body = "{'jsonBody':'valueExample'}";

        when(restConnectorMock.get("/some-path/12345?param1=value1")).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.acceptEncoding(any(ContentEncoding.class))).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.encodeWith(any(ContentEncoding.class))).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.accept(anyString())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.asContentType(anyString())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.withBody(any())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.withHeaders(any())).thenReturn(httpRequestBuilderMock);
        when(httpResponseMock.getStatus()).thenReturn(HttpStatus.OK);
        when(httpRequestBuilderMock.execute()).thenReturn(httpResponseMock);

        Connector connector = buildConnector(null, null);

        // Re-stubbing path method to simulate path variable usage
        when(clientResourcePropertiesMock.path()).thenReturn("/some-path/{id}");

        Connector.RequestBuilder requestBuilder = connector.ofResource("exampleTag");
        requestBuilder.body(body);
        requestBuilder.pathVariable("id", "12345"); // Example path variable
        HttpResponse httpResponse = requestBuilder.execute();

        assertEquals(httpResponseMock, httpResponse);

        verify(restConnectorMock).get("/some-path/12345?param1=value1");
        verify(httpRequestBuilderMock).acceptEncoding(ContentEncoding.GZIP);
        verify(httpRequestBuilderMock).encodeWith(ContentEncoding.GZIP);
        verify(httpRequestBuilderMock).accept("application/json");
        verify(httpRequestBuilderMock).asContentType("application/json");
        verify(httpRequestBuilderMock).withBody(body);
        verify(httpRequestBuilderMock).withHeaders(any());
        verify(httpRequestBuilderMock, never()).withResponseTimeout(anyLong());
        verify(httpRequestBuilderMock).execute();
        verify(clientResourcePropertiesMock).path();
    }

    @Test
    void getExecuteTest_withMultiplePathVariables_ok() throws IOException {
        String body = "{'jsonBody':'valueExample'}";

        when(restConnectorMock.get("/products/hotel/12345?param1=value1")).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.acceptEncoding(any(ContentEncoding.class))).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.encodeWith(any(ContentEncoding.class))).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.accept(anyString())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.asContentType(anyString())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.withBody(any())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.withHeaders(any())).thenReturn(httpRequestBuilderMock);
        when(httpResponseMock.getStatus()).thenReturn(HttpStatus.OK);
        when(httpRequestBuilderMock.execute()).thenReturn(httpResponseMock);

        Connector connector = buildConnector(null, null);

        // Re-stubbing path method to simulate path variable usage
        when(clientResourcePropertiesMock.path()).thenReturn("/products/{productType}/{id}");

        Connector.RequestBuilder requestBuilder = connector.ofResource("exampleTag");
        requestBuilder.body(body);
        requestBuilder.pathVariables(Map.of("id", "12345", "productType", "hotel"));
        HttpResponse httpResponse = requestBuilder.execute();

        assertEquals(httpResponseMock, httpResponse);

        verify(restConnectorMock).get("/products/hotel/12345?param1=value1");
        verify(httpRequestBuilderMock).acceptEncoding(ContentEncoding.GZIP);
        verify(httpRequestBuilderMock).encodeWith(ContentEncoding.GZIP);
        verify(httpRequestBuilderMock).accept("application/json");
        verify(httpRequestBuilderMock).asContentType("application/json");
        verify(httpRequestBuilderMock).withBody(body);
        verify(httpRequestBuilderMock).withHeaders(any());
        verify(httpRequestBuilderMock, never()).withResponseTimeout(anyLong());
        verify(httpRequestBuilderMock).execute();
        verify(clientResourcePropertiesMock).path();
    }

    @Test
    void getExecuteTest_withPathVariable_errorDueToNoClosingBrace() {
        String body = "{'jsonBody':'valueExample'}";

        Connector connector = buildConnector(null, null);

        // Re-stubbing path method to simulate path variable usage
        when(clientResourcePropertiesMock.path()).thenReturn("/some-path/{id");

        Connector.RequestBuilder requestBuilder = connector.ofResource("exampleTag");
        requestBuilder.body(body);
        requestBuilder.pathVariable("id", "12345"); // Example path variable

        RuntimeException thrownException = assertThrows(RuntimeException.class, requestBuilder::execute);
        assertEquals(
            "Unexpected end of path: Closing curly brace is missing on path '/some-path/{id'",
            thrownException.getMessage());

        verify(restConnectorMock, never()).get(anyString());
        verifyNoInteractions(httpRequestBuilderMock);
        verify(clientResourcePropertiesMock).path();
    }

    @Test
    void getExecuteTest_withPathVariable_errorDueToEmptyVariableName() {
        String body = "{'jsonBody':'valueExample'}";

        Connector connector = buildConnector(null, null);

        // Re-stubbing path method to simulate path variable usage
        when(clientResourcePropertiesMock.path()).thenReturn("/some-path/{}");

        Connector.RequestBuilder requestBuilder = connector.ofResource("exampleTag");
        requestBuilder.body(body);
        requestBuilder.pathVariable("id", "12345"); // Example path variable

        RuntimeException thrownException = assertThrows(RuntimeException.class, requestBuilder::execute);
        assertEquals("Path Variable name is empty on path '/some-path/{}'", thrownException.getMessage());

        verify(restConnectorMock, never()).get(anyString());
        verifyNoInteractions(httpRequestBuilderMock);
        verify(clientResourcePropertiesMock).path();
    }

    @Test
    void getExecuteTest_withPathVariable_errorDueToNoGivenReplacement() {
        String body = "{'jsonBody':'valueExample'}";

        Connector connector = buildConnector(null, null);

        // Re-stubbing path method to simulate path variable usage
        when(clientResourcePropertiesMock.path()).thenReturn("/some-path/{id}");

        Connector.RequestBuilder requestBuilder = connector.ofResource("exampleTag");
        requestBuilder.body(body);

        RuntimeException thrownException = assertThrows(RuntimeException.class, requestBuilder::execute);
        assertEquals("Path Variable value is not given for 'id'", thrownException.getMessage());

        verify(restConnectorMock, never()).get(anyString());
        verifyNoInteractions(httpRequestBuilderMock);
        verify(clientResourcePropertiesMock).path();
    }

    @Test
    void getExecuteTest_withPathVariable_errorDueToGivenNullAsReplacement() {
        String body = "{'jsonBody':'valueExample'}";

        Connector connector = buildConnector(null, null);

        // Re-stubbing path method to simulate path variable usage
        when(clientResourcePropertiesMock.path()).thenReturn("/some-path/{id}");

        Connector.RequestBuilder requestBuilder = connector.ofResource("exampleTag");
        requestBuilder.body(body);
        requestBuilder.pathVariable("id", null); // Passing null as replacement

        RuntimeException thrownException = assertThrows(RuntimeException.class, requestBuilder::execute);
        assertEquals("Path Variable value is not given for 'id'", thrownException.getMessage());

        verify(restConnectorMock, never()).get(anyString());
        verifyNoInteractions(httpRequestBuilderMock);
        verify(clientResourcePropertiesMock).path();
    }

    @Test
    void getExecuteTest_error() throws IOException {
        String body = "{'jsonBody':'valueExample'}";

        when(restConnectorMock.get(anyString())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.acceptEncoding(any(ContentEncoding.class))).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.encodeWith(any(ContentEncoding.class))).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.accept(anyString())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.asContentType(anyString())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.withBody(any())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.withHeaders(any())).thenReturn(httpRequestBuilderMock);
        when(httpRequestBuilderMock.execute()).thenThrow(IOException.class);

        Connector connector = buildConnector(null, "path");
        Connector.RequestBuilder requestBuilder = connector.ofResource("exampleTag");
        requestBuilder.body(body);

        assertThrows(ConnectorException.class, requestBuilder::execute);

        verify(httpRequestBuilderMock).acceptEncoding(ContentEncoding.GZIP);
        verify(httpRequestBuilderMock).encodeWith(ContentEncoding.GZIP);
        verify(httpRequestBuilderMock).accept("application/json");
        verify(httpRequestBuilderMock).asContentType("application/json");
        verify(httpRequestBuilderMock).withBody(body);
        verify(httpRequestBuilderMock).withHeaders(any());
        verify(httpRequestBuilderMock, never()).withResponseTimeout(anyLong());
        verify(httpRequestBuilderMock).execute();
    }

    private Connector buildConnector(Duration responseTimeout, String path) {
        when(clientResourcePropertiesMock.method()).thenReturn(HttpMethod.GET);
        if(path != null) {
            when(clientResourcePropertiesMock.path()).thenReturn(path);
        }
        when(clientResourcePropertiesMock.mediaType()).thenReturn("application/json");
        when(clientResourcePropertiesMock.responseTimeout()).thenReturn(responseTimeout);
        when(clientResourcePropertiesMock.encoding()).thenReturn(ContentEncoding.GZIP);
        when(clientResourcePropertiesMock.headers())
            .thenReturn(Collections.singletonMap("headerKey1", "headerValue1"));
        when(clientResourcePropertiesMock.params())
            .thenReturn(Collections.singletonMap("param1","value1"));
        Map<String, ClientResourceProperties> resourcesMock =
            Collections.singletonMap("exampleTag", clientResourcePropertiesMock);
        return new Connector("geoTest", restConnectorMock, resourcesMock);
    }
}
