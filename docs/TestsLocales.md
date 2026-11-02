# Tests locales

### Build

Al trabajar con containers es posible levantar la aplicación con docker en nuestras máquinas locales. Pero primero es necesario buildear la imagen docker a probar. Para ellos podemos utilizar el siguiente comando:

````docker build -t java-template:1.0 .````

La imagen base de este template utiliza una imagen docker de Despegar que contiene algunas dependencias de nuestra plataforma, como son certificados privados. Para más información y consejos al respecto ver este [link](https://github.com/despegar/nebula-onboarding/wiki/Consejos-para-dockerizar-aplicaci%C3%B3n-para-N%C3%A9bula#pruebas-locales)

### Run

Una vez generada la imagen docker ya podemos crear el container y hacer pruebas. Para esto usaremos docker-compose que nos va a permitir definir en un archivo llamado docker-compose.yml cómo crear el container, qué volúmenes locales montarle y otras cosas más.

Para el docker-compose.yml de este repositorio se puede crear el container de la siguiente manera:

```ENVIRONMENT=test TAG_VERSION=1.0 docker-compose up```

Donde *test* es el environment utilizado para tomar la configuración y secretos (ver carpeta support) y 1.0 es la versión de la imagen buildeada anteriormente.

### Secretos

Para cumplir con el [contrato de Nébula](https://github.com/despegar/nebula-onboarding/wiki/Contrato-entre-N%C3%A9bula-y-las-aplicaciones), entre otras cosas, la aplicación tiene que leer los secretos dentro del container en el path /service/secrets/sensitive.conf. Esto es porque la plataforma se encargará de inyectar dicho archivo en el container a partir de los secretos configurados para el servicio en cuestión y para el environment configurado. Por lo tanto, para probar en local es posible colocar los secretos necesarios en la carpeta ./support/[env]/sensitive.conf para que al ejecutarse docker-compose simulemos el comportamiento de Nébula. El patrón "**/*sensitive.conf" está incluído en .gitignore así que no será subido a Github.
