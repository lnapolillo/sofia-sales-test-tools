# Headers estandars

### Identificar clientes y servicios
- **X-Client:** Header requerido que indica quien nos esta consumiendo, si no viene se retorna un Bad Request (la validación es configurable por property).
- **X-Service:** Header agregado por la aplicación a la respuesta, que indica qué servicio se esta usando para poder trackear en PerfDB.
Por default, se usa el nombre del controller + nombre del método, pero se puede definir un nombre custom usando [@CustomServiceName](https://github.com/despegar/java-template/blob/routing/src/main/java/com/despegar/javatemplate/util/web/interceptor/CustomServiceName.java).
- **X-Forwarded-For:** IP desde donde vino el request. Se utiliza el que nos viene en el header, si no viene se intenta obtener del request. Se guarda en el RSD para agregarlo al log.

### Identificar un request
Para identificar la traza de un request distribuída a través de varias aplicaciones se utilizan dos headers:

- **X-UOW:** Valor que relaciona un conjunto de requests asociados al mismo request raíz. Se utiliza el que nos viene en el header del request, si no viene se genera uno y este sería la raíz del mismo. Se guarda en el RSD para propagarlo y agregarlo al log.
- **X-RequestId:** Valor que identifica un request. Se genera un **nuevo valor** en cada request y se reutiliza en todas las llamadas de ese salto. Se guarda en el RSD para agregarlo al log.

NOTA: La combinación de uow + request id genera un valor que permite identificar un request unívocamente.

### Derivar tráfico
- **X-Version-Override:** e utiliza para redirigir el tráfico de un request HTTP a un deployment group específico de un servicio en Nebula. Se guarda en el RSD para propagarlo.
- **XDESP-SANDBOX:** se utiliza para indicar que el tráfico debe dirigirse al ambiente sandbox de Nebula. Se guarda en el RSD para propagarlo.

### Otros
- **Headers Despegar:** Son headers con el prefijo "XDESP-" o "XD-" de uso particular de cada app. Se guardan en el RSD para propagarlos.

Tanto los headers para derivar tráfico como los headers "Despegar" funcionan dentro de la red de desarrollo de Despegar, para requests provenientes de internet se filtran en los balancers de frontera.
