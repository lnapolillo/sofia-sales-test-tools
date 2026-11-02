# Configuracion para reportar a NewRelic

## Pasos para configurar una nueva app
1. Reemplazar en todos los newrelic.yml de todos los ambientes los siguientes campos:
   1. app_name: My Application -> Seleccionar el nombre con el cual va a aparecer en NewRelic
   2. license_key: '<%= license_key %>' -> Poner el numero de licencia de la cuenta de NewRelic a reportar con las comillas, quedando algo como license_key: 'nroLicencia' 