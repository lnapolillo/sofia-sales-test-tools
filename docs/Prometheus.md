
# Prometheus

Se utiliza [Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) para exponer las métricas.
Y [Micrometer](https://micrometer.io/docs) para generarlas.

Para poder utilizar estas métricas que generamos, es necesario tener Prometheus y Grafana. Esto será responsabilidad de cada equipo/gerencia.

Además, recomendamos registrar en el post install de la aplicación el registro de la app en Prometheus, para que venga a consumir nuestras métricas generadas.


### Actuator

Consultando *"/actuator"* se puede ver lo que tenemos activado.

- Métricas que expone la app: *"/actuator/metrics"*

- Detalle de una métrica particular: *"/actuator/metrics/{metricName}"*

- Métricas con el formato para ser consumidas por prometheus: *"/actuator/prometheus"*

Como se puede ver en la documentación,
[Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) nos permite activar más cosas configurables mediante properties,
en nuestro caso decidimos la siguiente configuración básica, pero recomendamos analizar en cada caso:
```
management.endpoints.web.exposure.include=metrics,prometheus
management.endpoint.metrics.enabled=true
management.endpoint.prometheus.enabled=true
management.metrics.export.prometheus.enabled=true
```
Con esta configuración podremos tener métricas de la JVM y de Jetty, además de las que generemos nosotros, como veremos más adelante.


### Counters
Mediante el service [PrometheusService](https://github.com/despegar/java-template/blob/main/src/main/java/com/despegar/javatemplate/service/PrometheusService.java),
podremos gestionar contadores propios, con tags particulares. También se podrían implementar, de ser necesario, otro tipo de métricas, como por ejemplo gauges.


### @Timed
Esta annotation permite medir el tiempo de ejecución de un método y calcular los percentiles que necesitemos,
para luego crear nuestros dashboards. Recomendamos utilizarlo en los puntos de entrada (Controllers) y salida (Clients, Repositories) de la app.
Se utiliza de la siguiente manera:
```
@Timed(percentiles = { 0.5, 0.8, 0.95, 0.99, 0.999 }, histogram = true, value = "name")
```

### Dashboards
Podemos crear nuestros propios dashboards, pero además Grafana nos provee templates que pueden ser de gran ayuda, como [por ejemplo](https://grafana.com/grafana/dashboards/12464). 

Recomendamos como buena práctica, almacenar un export de nuestros dashboards en el repositorio de la app, para tener a modo de backup.