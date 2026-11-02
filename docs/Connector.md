# Connector
Conector configurable que utiliza la dependencia [rest-connector](https://github.com/despegar/rest-connector) de despegar

## Creación de un Connector
Para crear un Connector vamos a tener que definirlo via configuración, y definir los Beans de Spring correspondientes

### Configuración del Connector
Donde `prefix` corresponde al prefijo elegido para identificar el connector en la config 

| Property                                                                  | Tipo de dato          | Descripción | Default |
| ---------------------------------                                         | --------------        | ----------- | ----------- |
| `[prefix].rest-connector.host`                                            | String                | Host del conector, opcional si esta definido `endpoint` | - |
| `[prefix].rest-connector.endpoint`                                        | String                | Endpoint del conector (protocolo + host), opcional si esta definido `host` | - |
| `[prefix].rest-connector.secure`                                          | Boolean (opcional)    | Flag para indicar si el conector es seguro, donde `false: usa el protocolo http` y `true: usa el protocolo https e inicia el contexto SSL` | `false` |
| `[prefix].rest-connector.shutdown-hook-enabled`                           | Boolean (opcional)    | ? | `false` |
| `[prefix].rest-connector.max-connections`                                 | Integer (opcional)    | Conexiones máximas del conector | `20` |
| `[prefix].rest-connector.connection-timeout`                              | Duration (opcional)   | Timeout de conexión | `500 millis` |
| `[prefix].rest-connector.read-timeout`                                    | Duration (opcional)   | Timeout de lectura | `30 seconds` |
| `[prefix].rest-connector.idle-connection-timeout`                         | Duration (opcional)   | Timeout de conexión inactiva | `1 minute` |
| `[prefix].rest-connector.cache.enabled`                                   | Boolean (opcional)    | Flag que permite hablitar la cache del conector - depende de guava [ver javadoc](https://guava.dev/releases/14.0/api/docs/com/google/common/cache/CacheBuilder.html) | `false` |
| `[prefix].rest-connector.cache.size`                                      | Integer (opcional)    | Tamaño maximo de registros en la cache (propiedad de guava) | `1000` |
| `[prefix].rest-connector.cache.supported-methods`                         | List (opcional)       | Métodos soportados por la cache | `[MAX_AGE, EXPIRES, ETAG, LAST_MODIFIED]` |
| `[prefix].rest-connector.cache.concurrency-level`                         | Integer (opcional)    | Nivel de concurrecia de la cache (propiedad de guava) | `50` |
| `[prefix].rest-connector.json.format`                                     | String (opcional)     | Formato del json que va a usar el conector para la comunicación - valores soportados: `CAMEL_CASE`, `SNAKE_CASE` | `SNAKE_CASE` |
| `[prefix].rest-connector.json.zoned-date-time-pattern`                    | String (opcional)     | Patrón custom para la serialización de ZonedDateTime | - |
| `[prefix].rest-connector.json.local-date-time-pattern`                    | String (opcional)     | Patrón custom para la serialización de LocalDateTime | - |
| `[prefix].rest-connector.json.local-date-pattern`                         | String (opcional)     | Patrón custom para la serialización de LocalDate | - |
| `[prefix].rest-connector.json.local-time-pattern`                         | String (opcional)     | Patrón custom para la serialización de LocalTime | - |
| `[prefix].rest-connector.validate-after-inactivity`                       | Integer (opcional)    | Tiempo de inactividad para revalidar la conexión del conector | - |
| `[prefix].rest-connector.request.max-retries`                             | Integer (opcional)    | Cantidad de reintentos de un request antes de fallar | `1` |
| `[prefix].rest-connector.x-version`                                       | String (opcional)     | Header `X-Version` de despegar | - |
| `[prefix].rest-connector.client-id`                                       | String (opcional)     | Header `X-Client` de despegar | - |

### Configuración de los recursos del Connector
Tenemos la posibilidad de definir los recursos del conector a partir de una configuración

Donde `prefix` corresponde al prefijo elegido para identificar el connector en la config, y `resource` es el nombre del recurso definido

| Property                                                     | Tipo de dato        | Descripción                                                                                                                                              | Default |
|--------------------------------------------------------------|---------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------| ----------- |
| `[prefix].resources.[resource].name`                         | String              | Nombre para identificar el recurso, debe ser único por cada conector                                                                                     | - |
| `[prefix].resources.[resource].method`                       | String              | Método http - valores soportados: GET, PUT, POST, DELETE, PATCH                                                                                          | - |
| `[prefix].resources.[resource].path`                         | String              | Path del recurso                                                                                                                                         | - |
| `[prefix].resources.[resource].media-type`                   | String (opcional)   | Define los headers `Content-Type` y `Accept`                                                                                                             | - |
| `[prefix].resources.[resource].encoding`                     | String (opcional)   | Define el header `Content-Encoding`                                                                                                                      | - |
| `[prefix].resources.[resource].headers.[header-key]`         | String (opcional)   | Define el header del `header-key`                                                                                                                        | - |
| `[prefix].resources.[resource].params.[param-key]`           | String (opcional)   | Define el query param del `param-key`                                                                                                                    | - |
| `[prefix].resources.[resource].response-timeout` | Duration (opcional) | Define el response timeout a aplicar para este resource en particular. Overriddea el valor definido a nivel rest-connector | - |

### Definición de Beans
Para crear un Connector vamos a tener que definir los siguientes Beans:
- Bean de la configuración
- Bean del Connector

#### Bean de la configuración
Para definir la configuración vamos a tener que extender ConnectorProperties y definir un prefijo para la config

Definición, donde elegimos `my-connector` como `prefix`
```java
    @ConstructorBinding
    @ConfigurationProperties(prefix = "my-connector")
    public static class MyConnectorProperties extends ConnectorProperties {
        public MyConnectorProperties(RestConnectorProperties restConnector,
                                      Map<String, ClientResourceProperties> resources) {
            super(restConnector, resources);
        }
    }
```
```properties
my-connector.rest-connector.host = proxy
my-connector.rest-connector.read-timeout = 2 seconds
my-connector.rest-connector.json.format = SNAKE_CASE
my-connector.resources.my-resource.name = my-resource
my-connector.resources.my-resource.method = GET
my-connector.resources.my-resource.path = /my-resource
my-connector.resources.my-resource.encoding = GZIP
my-connector.resources.my-resource.media-type = application/json
my-connector.resources.my-resource.params.my-param = some-value
my-connector.resources.my-resource.response-timeout = 1s
```

#### Bean del Connector
Para definir el Connector vamos a hacer uso de ConnectorFactory y la configuración definida

Ejemplo
```java
public class ConnectorConfig {
    @Bean("myConnector")
    public Connector myConnector(ConnectorFactory factory, MyConnectorProperties properties) {
        return factory.create("my-connector", properties);
    }
}
```

Ejemplo con custom Interceptor
```java
public class ConnectorConfig {
    @Bean("myConnector")
    public Connector myConnector(ConnectorFactory factory, MyConnectorProperties properties) {
        return factory.create("my-connector", properties, List.of(new MyCustomInterceptor()));
    }
}
```

### Uso
Podemos usar el connector desde el recurso de diferentes formas:
- Consumiendo el recurso definido en la configuración
- Armando el recurso programáticamente
- Consumiendo el recurso de la config y modificándolo programáticamente

#### Consumiendo el recurso definido en la configuración
La clase `Connector` cuenta con el método `ofResource(name)` que nos permite crea un RequestBuilder a partir del nombre del recurso configurado

Ejemplo
```java
public class MyClient {
    public Response get() {
        return connector
                .ofResource("my-resource")
                .execute(GeoCountries.class);
    }
}
```
Siguiendo el ejemplo de configuración estaríamos ejecutando el siguiente request `GET http://proxy/my-resource?my-param=some-value [Headers = Content-Encoding:gzip, Accept:application/json, Content-Type:application/json]` 

#### Armando el recurso programáticamente
La clase `Connector` cuenta con un método para cada verbo http `get(path)`, `post(path)`, `put(path)`, `patch(path)`, `delete(path)` que nos permite crea un RequestBuilder

Ejemplo
```java
public class MyClient {
    public Response get() {
        return connector
                .get("/my-resource")
                .param("my-param", "some-value")
                .encoding(ContentEncoding.GZIP)
                .mediaType("application/json")
                .execute(GeoCountries.class);
    }
}
```
Siguiendo el ejemplo de configuración estaríamos ejecutando el siguiente request `GET http://proxy/my-resource?my-param=some-value [Headers = Content-Encoding:gzip, Accept:application/json, Content-Type:application/json]` 

#### Consumiendo el recurso de la config y modificándolo programáticamente
Si creamos un RequestBuilder a partir de un recurso configurado, podemos modificarlo programáticamente para agregar params, headers, etc

Ejemplo
```java
public class MyClient {
    public Response get() {
        return connector
                .ofResource("my-resource")
                .param("other-param", "other-value")
                .execute(GeoCountries.class);
    }
}
```
Siguiendo el ejemplo de configuración estaríamos ejecutando el siguiente request `GET http://proxy/my-resource?my-param=some-value&other-param=other-value [Headers = Content-Encoding:gzip, Accept:application/json, Content-Type:application/json]` 
