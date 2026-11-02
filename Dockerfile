# [Dockerfile] - https://docs.docker.com/engine/reference/builder/
# [best-practices] - https://docs.docker.com/develop/develop-images/dockerfile_best-practices/#use-multi-stage-builds
# [multi-stage-build] - https://docs.docker.com/develop/develop-images/multistage-build/
# Esta imagen es descartable. Los objetivos son:
# 1. Descargar dependencias
# 2. Compilar
# 3. Buildear el jar
# https://github.com/despegar/nebula-onboarding/wiki/Docker:-Im%C3%A1genes-provistas-por-Nebula
FROM hub.nebula.prod.us-east-1.despegar.net/maven:jdk21_2.0.1 AS build

# Copiamos el pom para exclusivamente descargar las dependencias y que pueda guardase este layer en la cache. [cache-layers]: https://docs.docker.com/develop/develop-images/dockerfile_best-practices/#leverage-build-cache
# HELP: COPY <archivo_de_nuestro_directorio> <destino_dentro_de_la_imagen_*build*>
COPY pom.xml /app/pom.xml

# Con el comando WORKDIR cambiamos de directorio
# HELP: WORKDIR <nuevo_directorio_de_trabajo>
WORKDIR /app/

# Descargamos dependencias exclusivamente para que pueda guardarse en la cache si no hubo cambios en el pom
# HELP: RUN <comandos a ejecutar>
RUN mvn dependency:go-offline

# Copio exclusivamente el directorio para compilarlo
# HELP: COPY <archivo_de_nuestro_directorio> <destino_dentro_de_la_imagen_*build*>
COPY src /app/src

# Buildeamos el jar SIN ejecutar los tests
# HELP: RUN <comandos a ejecutar>
RUN mvn package -Dmaven.test.skip=true


#Esta imagen es la que se va a deployar en nebula. Los objetivos son:
# 1. Imagen de base liviana.
# 2. Instalar herramientas para troubleshooting.
# 3. Copiar de la imagen de *build* los archivos necesarios para ejecutar correctamente la aplicacion
# https://github.com/despegar/nebula-onboarding/wiki/Docker:-Im%C3%A1genes-provistas-por-Nebula
FROM hub.nebula.prod.us-east-1.despegar.net/java-21-corretto:3.0.0 AS final

WORKDIR /tmp

# Check & update the TZData version.
# NOTE: We use `ADD` instead of `RUN wget` to avoid caching issues.
ADD "https://data.iana.org/time-zones/tzdata-latest.tar.gz" ${TZ_PATH}/tzdata-latest.tar.gz

RUN java -jar ${TZ_PATH}/ziupdater.jar -V && \
    java -jar ${TZ_PATH}/ziupdater.jar -l file:${TZ_PATH}/tzdata-latest.tar.gz && \
    java -jar ${TZ_PATH}/ziupdater.jar -V

RUN rm -rf ${TZ_PATH}/tzdata-latest.tar.gz

# Exponer el puerto 9290 donde nebula enviara el trafico a la aplicacion
# Exponer el puerto 9800 para que los componentes de nebula puedan acceder a las metricas de la JVM
EXPOSE 9290 9800

# Evita cambio de versión de la jvm
# https://askubuntu.com/questions/18654/how-to-prevent-updating-of-a-specific-package
RUN apt-mark hold java-21-amazon-corretto-jdk

WORKDIR /app

# Descargo las dependencias de la aplicación (si aplica)
# RUN apt update -y \
#    && apt install curl -y

# Habilito Jemalloc para mejorar el manejo de memoria nativa
RUN apt update -y && apt install -y libjemalloc2 libjemalloc-dev

# Directorio para guardar logs del GC
RUN mkdir -p /app/logs && chown $NBL_USER_ID /app/logs

# Borramos los agentes para estar seguros que no quedan agentes viejos
RUN rm -rf /app/agents/*.jar


# Luego de realizar todas las acciones requeridas con el usuario root cambiamos el usuario a uno NO Root para que la aplicación se ejecute con este usuario
# La imagen base hub.nebula/java-XX-corretto:X.X.X ya vienen con un usuario creado listo para usar, sólo es necesario ejecutar la siguiente instrucción
USER $NBL_USER_ID

# En este caso, copiamos de la imagen descartable el jar generado y el jar de newrelic a usar como agent que necesitamos en la imagen
# HELP: COPY <archivo_de_directorio_de_build> <destino_dentro_de_la_imagen_*final*>
COPY --from=build /app/target/java-template.jar java-template.jar

# Copiamos el agente de newrelic actualizado que se descargó en la etapa de build.
COPY --from=build /app/target/external-jars/newrelic-agent.jar agents/newrelic-agent.jar
# Copiamos el agente de promethus actualizado que se descargó en la etapa de build.
COPY --from=build /app/target/external-jars/jmx_prometheus_javaagent.jar agents/jmx_prometheus_javaagent.jar

# El script de startup se usa para ejecutar la aplicación parametrizada por el archivo jvm-args que es inyectado por nebula según el ambiente
COPY support/nebula/startup .

# Esta linea es la que ejecuta nuestra app y se recomienda ejecutar con CMD para propagar SIGTERM a la aplicacion y
# comenzar el proceso de graceful shutdown.
CMD ["/bin/bash", "-c", "./startup"]
