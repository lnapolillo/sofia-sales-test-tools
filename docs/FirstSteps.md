# Primeros pasos y ejemplos

En la carpeta **src/examples** se encuentran algunos ejemplos de uso de algunas de las features del template. En el caso de usar este proyecto como template es necesario **eliminar la carpeta *examples* completa**.

A continuación se describen los pasos que se deben seguir para personalizar el template para la creación de una nueva aplicación.


1. Crear nuevo repositorio usando el botón "Use this template".

   <img width="917" alt="Screen Shot 2022-01-25 at 16 55 52" src="https://user-images.githubusercontent.com/1668933/151247586-cad2ab90-ba76-4d30-94b6-3e8bb3fb172a.png">
2. Eliminar la carpeta **examples** que está dentro de **src**.
3. Eliminar el archivo CODEOWNERS.
4. Quitar del pom.xml el build-helper-maven-plugin que se encarga de agregar las carpetas de examples como sources a buildear por maven.
5. Modificar el nombre de la aplicación en:
   - artifactId dentro del pom.xml
   - application.clientId en el archivo src/main/resources/despegar.properties
   - APP_NAME dentro de la clase ServiceName
   - app_name en todos los newrelic.yml
6. Configurar Server (para más información ver la [documentación](JettyServer.md))
   - Tipo de executor y tamaño del pool.
   - Tipo de cola y tamaño.
7. Configurar log (para más información ver la [documentación](Logging.md))
   - Debe existir el archivo logback.xml para cada ambiente en la carpeta configuration/nebula/{env}/
   - Los logs deben enviarse directamente a través de stdout.
8. Configurar New Relic en los distintos ambientes (para más información ver la [documentación](NewRelic.md))
   - Nombre de aplicación (distinto por ambiente)
   - License key
   - Configuración general: Que excepciones reportar? que http status reportar? log level del agente, etc.
9. Configurar métricas que exponemos para Prometheus (para más información ver la [documentación](Prometheus.md))
10. Modificar el nombre del jar que vamos a generar en el script support/nebula/startup y el deployment-template. (para más información ver la [documentación](Nebula.md))
11. Configurar los workflows de GitHub Actions de Nebula CD
   - Completar el nombre del servicio en los archivos .github/workflows/nbl-build.yml y .github/workflows/nbl-deploy.yml (en jobs.build y jobs.deploy respectivamente).
   - Completar el jira_project en el archivo .github/workflows/nbl-deploy.yml (en jobs.deploy)
   - Para mas información ver la [documentación](https://github.com/despegar/nebula-onboarding/wiki/CICD)
    

El java template contiene el servicio de **health-check** que se puede verificar de esta forma:
http://localhost:9290/health-check 
y el **springdoc-openapi** que se puede invocar asi:
http://localhost:9290/swagger-ui/index.html
