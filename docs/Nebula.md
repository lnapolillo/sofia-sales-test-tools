# Aspectos relacionados a Nébula

Para poder deployar la aplicación en Nébula la misma debe poder empaquetarse en un container. A continuación nombramos algunos aspectos que son necesarios tener en cuenta para esto.

### Dockerfile

Para poder empaquetar la aplicación en una imagen docker deployable en Nebula es necesario contar con al menos un Dockerfile en el directorio raíz del proyecto. En este archivo debemos compilar y empaquetar la aplicación. 
Para esto es recomendable hacer el archivo multi-stage (una etapa para compilar y otra para generar la imagen docker final a utilizar en runtime).

Para más información ver [aquí](https://github.com/despegar/nebula-onboarding/wiki/Consejos-para-dockerizar-aplicaci%C3%B3n-para-N%C3%A9bula).

### Configuración

En Nébula, la configuración de la aplicación se recomienda fuertemente que no se incluya en la imagen docker a deployar, ya que la plataforma permite la inyección automática por ambiente como un volumen montado en el container. Para más información del contrato entre las aplicaciones y Nébula ver [aquí](https://github.com/despegar/nebula-onboarding/wiki/Contrato-entre-N%C3%A9bula-y-las-aplicaciones).

En el caso de este template, en la carpeta support se encuentra una subcarpeta por ambiente. Dentro de cada una de ellas encontraremos los archivos que Nébula nos montará dentro del path /service/config en nuestro container. Sólo montará la subcarpeta correspondiente al entorno de Nébula en donde se está deployando.

Por ejemplo, al deployar en el environment test en Nébula, la plataforma montará ./configuration/nebula/test/application.properties en el container en /service/config/application.properties, y lo mismo para la subcarpeta y su contenido ./configuration/nebula/test/newrelic en /service/config/newrelic.
