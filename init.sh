#!/bin/bash

# read project name (directory name as default)
read -p "Enter project name (default: $(basename "$PWD")): " project_name
read -p "Keep examples ? (y/N): " keep_examples
if [ -z "$project_name" ]; then
    project_name=$(basename "$PWD")
fi
# keep_examples is set to true if the user enters 'y' or 'Y' else false
if [[ "$keep_examples" == "y" || "$keep_examples" == "Y" ]]; then
    keep_examples=true
else
    keep_examples=false
fi

package_name=$(echo "$project_name" | tr '-' '_')

if [[ "$(uname)" == "Darwin" ]]; then
    # macOS
    sed -i '' "s/java-template/$project_name/g" pom.xml
    sed -i '' '/<!-- Plugin para agregar a la compilación los fuentes/,/<\/plugin>/d' pom.xml
    sed -i '' "s/java-template/$project_name/g" src/main/resources/despegar.properties
    sed -i '' "s/JAVA-TEMPLATE/$project_name/g" src/main/java/com/despegar/javatemplate/model/metric/ServiceName.java
    sed -i '' "s/java-template/$project_name/g" configuration/nebula/test/newrelic/newrelic.yml
    sed -i '' "s/java-template/$project_name/g" deployment-template-test.yaml
    sed -i '' "s/java-template/$project_name/g" Dockerfile
    sed -i '' "s/java-template/$project_name/g" support/nebula/startup
    find src/main/java -type f -name "*.java" -exec sed -i '' "s/com.despegar.javatemplate/com.despegar.$package_name/g" {} \;
    find src/examples/connector/java -type f -name "*.java" -exec sed -i '' "s/com.despegar.javatemplate/com.despegar.$package_name/g" {} \;
    find src/test/java -type f -name "*.java" -exec sed -i '' "s/com.despegar.javatemplate/com.despegar.$package_name/g" {} \;
else
    # Linux
    sed -i "s/java-template/$project_name/g" pom.xml
    sed -i '/<!-- Plugin para agregar a la compilación los fuentes/,/<\/plugin>/d'
    sed -i "s/java-template/$project_name/g" src/main/resources/despegar.properties
    sed -i "s/JAVA-TEMPLATE/$project_name/g" src/main/java/com/despegar/javatemplate/model/metric/ServiceName.java
    sed -i "s/java-template/$project_name/g" configuration/nebula/test/newrelic/newrelic.yml
    sed -i "s/java-template/$project_name/g" deployment-template-test.yaml
    sed -i "s/java-template/$project_name/g" Dockerfile
    sed -i "s/java-template/$project_name/g" support/nebula/startup
    find src/main/java -type f -name "*.java" -exec sed -i "s/com.despegar.javatemplate/com.despegar.$package_name/g" {} \;
    find src/examples/connector/java -type f -name "*.java" -exec sed -i "s/com.despegar.javatemplate/com.despegar.$package_name/g" {} \;
    find src/test/java -type f -name "*.java" -exec sed -i "s/com.despegar.javatemplate/com.despegar.$package_name/g" {} \;
fi

mv src/main/java/com/despegar/javatemplate src/main/java/com/despegar/$package_name
mv src/examples/connector/java/com/despegar/javatemplate src/examples/connector/java/com/despegar/$package_name
mv src/test/java/com/despegar/javatemplate src/test/java/com/despegar/$package_name

if [ "$keep_examples" = false ]; then
    rm -rf src/examples
else
  mv src/examples/connector/resources/connector.yaml src/main/resources/connector.yaml
  cp -r src/examples/connector/java/ src/main/java
  rm -rf src/examples
fi

rm -rf CODEOWNERS