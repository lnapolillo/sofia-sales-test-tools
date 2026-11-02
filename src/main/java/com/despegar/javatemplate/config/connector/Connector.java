package com.despegar.javatemplate.config.connector;

import com.despegar.javatemplate.config.connector.model.ClientResourceProperties;
import com.despegar.javatemplate.exception.ConnectorException;
import com.despegar.library.rest.HttpHeaders;
import com.despegar.library.rest.HttpMethod;
import com.despegar.library.rest.HttpResponse;
import com.despegar.library.rest.HttpStatus;
import com.despegar.library.rest.RestConnector;
import com.despegar.library.rest.builder.HttpRequestBuilder;
import com.despegar.library.rest.compression.ContentEncoding;
import com.despegar.library.rest.utils.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.despegar.library.rest.HttpStatus.OK;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

public record Connector(
    String name,
    RestConnector restConnector,
    Map<String, ClientResourceProperties> resourcesByName)
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Connector.class);

    public RequestBuilder get(String path) {
        return createRequestBuilder(HttpMethod.GET, path);
    }

    public RequestBuilder post(String path) {
        return createRequestBuilder(HttpMethod.POST, path);
    }

    public RequestBuilder put(String path) {
        return createRequestBuilder(HttpMethod.PUT, path);
    }

    public RequestBuilder patch(String path) {
        return createRequestBuilder(HttpMethod.PATCH, path);
    }

    public RequestBuilder delete(String path) {
        return createRequestBuilder(HttpMethod.DELETE, path);
    }

    public RequestBuilder ofResource(String resourceName) {
        ClientResourceProperties resource = ofNullable(resourcesByName.get(resourceName))
                .orElseThrow(() -> new ConnectorException("Connector resource " + resourceName + " is empty"));

        RequestBuilder builder = new RequestBuilder(name, restConnector, resource.method(), resource.path());

        ofNullable(resource.mediaType()).ifPresent(builder::mediaType);
        ofNullable(resource.encoding()).ifPresent(builder::encoding);
        ofNullable(resource.headers()).ifPresent(builder::headers);
        ofNullable(resource.params()).ifPresent(builder::params);
        ofNullable(resource.responseTimeout()).ifPresent(builder::responseTimeout);

        return builder;
    }

    private RequestBuilder createRequestBuilder(HttpMethod method, String path) {
        return new RequestBuilder(name, restConnector, method, path);
    }

    public static class RequestBuilder {
        private final String name;
        private final RestConnector restConnector;
        private final HttpMethod method;
        private final String path;

        private ContentEncoding encoding;
        private String mediaType;
        private Duration responseTimeout;
        private Map<String, String> headers;
        private Map<String, String> params;
        private Map<String, String> pathVariables;
        private Object body;

        public RequestBuilder(
            String name,
            RestConnector restConnector,
            HttpMethod method,
            String path)
        {
            this.name = name;
            this.restConnector = restConnector;
            this.method = method;
            this.path = path;
        }

        public RequestBuilder encoding(ContentEncoding encoding) {
            this.encoding = encoding;
            return this;
        }

        public RequestBuilder mediaType(String mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public RequestBuilder param(String key, String value) {
            if (this.params == null) {
                this.params = new HashMap<>();
            }
            this.params.put(key, value);
            return this;
        }

        public RequestBuilder params(Map<String, String> params) {
            if (this.params == null) {
                this.params = new HashMap<>();
            }
            this.params.putAll(params);
            return this;
        }

        public RequestBuilder pathVariable(String key, String value) {
            if (this.pathVariables == null) {
                this.pathVariables = new HashMap<>();
            }
            this.pathVariables.put(key, value);
            return this;
        }

        public RequestBuilder pathVariables(Map<String, String> pathVariables) {
            if (this.pathVariables == null) {
                this.pathVariables = new HashMap<>();
            }
            this.pathVariables.putAll(pathVariables);
            return this;
        }

        public RequestBuilder responseTimeout(Duration responseTimeout) {
            this.responseTimeout = responseTimeout;
            return this;
        }

        public RequestBuilder header(String key, String value) {
            if (this.headers == null) {
                this.headers = new HashMap<>();
            }
            this.headers.put(key, value);
            return this;
        }

        public RequestBuilder headers(Map<String, String> headers) {
            if (this.headers == null) {
                this.headers = new HashMap<>();
            }
            this.headers.putAll(headers);
            return this;
        }

        public RequestBuilder body(Object body) {
            this.body = body;
            return this;
        }

        public <T> T execute(Class<T> responseType) {
            return execute(responseType, OK);
        }

        public <T> T execute(TypeReference<T> responseType) {
            return execute(responseType, OK);
        }

        public <T> T execute(Class<T> responseType, HttpStatus... expectedStatus) {
            TypeReference<T> typeReference = new TypeReference<>() {
                @Override
                public Type getType() {
                    return responseType;
                }
            };
            return execute(typeReference, expectedStatus);
        }

        public <T> T execute(TypeReference<T> responseType, HttpStatus... expectedStatus) {
            try (HttpResponse response = execute()) {
                if (Arrays.asList(expectedStatus).contains(response.getStatus())) {
                    return response.getBodyAs(responseType);
                }

                throw new ConnectorException("Status " + response.getStatus().getCode() + ". " + response.getBody());
            } catch (IOException e) {
                throw new ConnectorException("Error on http " + method, e);
            }
        }

        public HttpResponse execute() {
            final var rq = buildHttpRequest();
            try {
                LOGGER.info("Executing {}", rq.printableUrlWithHeaders());
                final var rs = rq.execute();
                LOGGER.info("{} executed! response status: {}", rq.printableUrlWithHeaders(), rs.getStatus().getCode());
                return rs;
            } catch (IOException e) {
                throw new ConnectorException("Error on " + rq.printableUrl(), e);
            }
        }

        private HttpRequestBuilder buildHttpRequest() {
            String url = buildURL(
                path,
                Optional.ofNullable(pathVariables).orElse(Collections.emptyMap()),
                Optional.ofNullable(params).orElse(Collections.emptyMap()));
            HttpRequestBuilder builder = initHttpRequestBuilder(url);

            if (encoding != null) {
                builder = builder.acceptEncoding(encoding);
            }

            if (encoding != null && body != null) {
                builder = builder.encodeWith(encoding);
            }

            if (mediaType != null) {
                builder = builder.accept(mediaType).asContentType(mediaType);
            }

            if (body != null) {
                builder = builder.withBody(body);
            }

            if (responseTimeout != null) {
                builder = builder.withResponseTimeout(responseTimeout.toMillis());
            }

            builder = builder.withHeaders(toHttpHeaders.apply(headers));

            return builder;
        }

        private HttpRequestBuilder initHttpRequestBuilder(String url) {
            return switch (method) {
                case GET -> restConnector.get(url);
                case POST -> restConnector.post(url);
                case PUT -> restConnector.put(url);
                case DELETE -> restConnector.delete(url);
                case PATCH -> restConnector.patch(url);
                default -> throw new RuntimeException("Unsupported HTTP method: " + method);
            };
        }

        private String buildURL(String path, Map<String, String> pathVariables, Map<String, String> params) {
            //TODO: reemplazar todo esto por UriComponentsBuilder o algo asi
            return replacePathVariables(path, pathVariables) + queryString(params);
        }

        private String replacePathVariables(String path, Map<String, String> pathVariables) {
            int currentPosition = 0;
            StringBuilder builder = new StringBuilder();

            while (currentPosition < path.length()) {
                // Let's find the next opening brace
                int openingBracePosition = path.indexOf("{", currentPosition);

                if (openingBracePosition < 0) {
                    // There is no more opening brace, we can append the rest of the path
                    builder.append(path.substring(currentPosition));
                    // We then update the current position to the end of the path as to exit the loop
                    currentPosition = path.length();
                } else {
                    // We found an opening brace, we append the part of the path before it
                    builder.append(path, currentPosition, openingBracePosition);
                    // We then find the next closing brace
                    int closingBracePosition = path.indexOf("}", openingBracePosition);

                    if (closingBracePosition < 0) {
                        // No closing brace found, we throw an error
                        throw new RuntimeException(
                            "Unexpected end of path: Closing curly brace is missing on path '" + path + "'");
                    }
                    // We found a closing brace, we can extract the variable name
                    String variableName = path.substring(openingBracePosition + 1, closingBracePosition);

                    if (StringUtils.isBlank(variableName)){
                        // If the variable name is empty, we throw an error
                        throw new RuntimeException("Path Variable name is empty on path '" + path + "'");
                    }
                    // If the variable name is not empty, we can look for its value at the map of path variables
                    String valueForReplacement = pathVariables.get(variableName);

                    if (isNull(valueForReplacement)) {
                        // There is no value for the variable, we throw an error
                        throw new RuntimeException(
                            "Path Variable value is not given for '" + variableName + "'");
                    }
                    // If we found a value for the variable, we append it to the buffer instead of the variable name
                    builder.append(URLEncoder.encode(valueForReplacement));
                    // We then move to the position after the closing brace
                    currentPosition = closingBracePosition + 1;
                }
            }
            return builder.toString();
        }

        private String queryString(Map<String, String> queryArgs) {
            String query = queryArgs.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().isEmpty())
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> URLEncoder.encode(entry.getKey()) + "=" + URLEncoder.encode(entry.getValue()))
                .collect(joining("&"));

            return query.isEmpty() ? StringUtils.EMPTY : "?" + query;
        }

        private static final Function<Map<String, String>, HttpHeaders> toHttpHeaders = headersMap -> {
            HttpHeaders httpHeaders = new HttpHeaders();
            if (headersMap != null) {
                httpHeaders.setAll(headersMap);
            }
            return httpHeaders;
        };
    }
}
