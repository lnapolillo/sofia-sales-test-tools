# Thread Pools
## Crear un ThreadPoolExecutor desde una config
El objetivo de la creación a partir de una config es permitir definir diferentes configuraciones de ThreadPoolExecutor evitando tener que hacer una release para cada cambio, por ejemplo, en el caso de realizar pruebas de carga

### Configuración de ExecutorService
Donde `prefix` es el nombre de fantasia para identificar cada executor en la config

Cuando definimos la configuración que se detalla a continuación, `ThreadPoolConfiguration.register(...)` se encarga de registrar cada bean del tipo ThreadPoolExecutor de forma dinámica.

| Property                                                        | Tipo de dato       | Descripción | Default |
| ---------------------------------------------------             | --------------     | ----------- | ----------- |
| `thread-pool.executors.[prefix].name`                           | String             | Nombre del thread. Se convierte en camel case y se usa como identificador del executor y del bean | - |
| `thread-pool.executors.[prefix].core-pool-size`                 | Integer            | Cantidad de threads que se mantendrán en el pool, incluso estando inactivos | - |
| `thread-pool.executors.[prefix].maximum-pool-size`              | Integer            | Cantidad maxima de threads disponibles para el pool | - |
| `thread-pool.executors.[prefix].keep-alive-time`                | Integer            | Cuando `maximum-pool-size` es mayor que `core-pool-size`, es el tiempo máximo en ms que los threads inactivos esperan nuevas tareas antes de terminar | - |
| `thread-pool.executors.[prefix].queue.[type]`                   | Property           | Implementación de BlockingQueue usada en el thread-pool <br> Keys posibles: <br> - `linked` instancia `java.util.concurrent.LinkedBlockingQueue`, requiere la config `server.jetty.thread-pool.[server.jetty.thread-pool.type].queue.linked.*` <br> - `array` instancia `java.util.concurrent.ArrayBlockingQueue`, requiere la config `server.jetty.thread-pool.[server.jetty.thread-pool.type].queue.array.*` <br> - `jetty-array` instancia `org.eclipse.jetty.util.BlockingArrayQueue`, requiere la config `server.jetty.thread-pool.[server.jetty.thread-pool.type].queue.jetty-array.*` <br> - `synchronous` instancia `java.util.concurrent.SynchronousQueue`, requiere la config `server.jetty.thread-pool.[server.jetty.thread-pool.type].queue.synchronous.*` | - |
| `thread-pool.executors.[prefix].queue.linked.capacity`          | Integer (opcional) | Capacidad de la cola | `Integer.MAX_VALUE` |
| `thread-pool.executors.[prefix].queue.array.capacity`           | Integer            | Capacidad de la cola | - |
| `thread-pool.executors.[prefix].queue.array.fair`               | Boolean (opcional) | Política de acceso <br> `fair=true`: los accesos a la cola para subprocesos bloqueados en la inserción o eliminación se procesan en orden FIFO <br> `fair=false`: la orden de acceso sin especificar | `false` |
| `thread-pool.executors.[prefix].queue.jetty-array.capacity`     | Integer (opcional) | Capacidad de la cola. Si no se define `max-capacity` y `grow-by`, se usa también como `max-capacity` | `128` |
| `thread-pool.executors.[prefix].queue.jetty-array.grow-by`      | Integer (opcional) | Factor de crecimiento de la cola. Obligatorio si se define `capacity` y `max-capacity` | `64` |
| `thread-pool.executors.[prefix].queue.jetty-array.max-capacity` | Integer (opcional) | Capacidad maxima de la cola | `Integer.MAX_VAlUE` |
| `thread-pool.executors.[prefix].queue.synchronous.fair`         | Boolean (opcional) | Política de acceso <br> `fair=true`: los accesos a la cola para subprocesos bloqueados en la inserción o eliminación se procesan en orden FIFO <br> `fair=false`: la orden de acceso sin especificar | `false` |

### Ejemplo
`ThreadPoolExecutor` con `LinkedBlockingQueue`
```properties
thread-pool.executors.example.maximum-pool-size=240
thread-pool.executors.example.core-pool-size=120
thread-pool.executors.example.keep-alive-time=60000
thread-pool.executors.example.thread-name-prefix=example-thread-pool
thread-pool.executors.example.queue.linked.capacity=10
```

Es equivalente a la siguiente definición
```java
public class ThreadPoolConfiguration {

    @Bean("exampleThreadPool")
    public ThreadPoolExecutor exampleThreadPool() {
        return new ThreadPoolExecutor(120, 240, 600000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10), new CustomizableThreadFactory("exampleThreadPool"));
    }
}
```

### Implementación
Revisar [com.despegar.javatemplate.config.thread.ThreadPoolConfiguration](../src/main/java/com/despegar/javatemplate/config/thread/ThreadPoolConfiguration.java)

## Métricas de ThreadPool
Podemos agregar metricas de los thread pools declarados agregando la config
```properties
thread-pool.notifier.schedule-in-seconds=10
```
Donde el valor representa la frecuencia del trackeo

Registro automatico de cada thread-pool al notificador:
- El thread-pool de jetty
- Cada thread-pool declarado con la config `thread-pool.executors`

En el caso que definamos thread-pool por fuera de la config vamos a tener que agregar manualmente el pool a `PoolMetricsNotifier` via `addPool(...)`

Utilizamos la libreria de despegar [threadpool-insights-metrics](https://github.com/despegar/threadpool-insights-metrics).

### Implementación
Revisar [com.despegar.javatemplate.config.thread.ThreadPoolConfiguration](../src/main/java/com/despegar/javatemplate/config/thread/ThreadPoolConfiguration.java)
