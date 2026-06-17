#!/bin/bash

# 1. Start SSH
service ssh start

# 2. Start NGINX
service nginx start

# 3. Clone or pull the Spring Boot project
cd /opt
if [ -d "app" ]; then
    cd app
    git fetch origin
    git reset --hard origin/master
else
    git clone -b master https://github.com/Th3ngSer/Final_DevOps.git app
    cd app
fi

# 4. Build and run Spring Boot
cd scratch_app
mkdir -p src/main/resources/graphql-client
mvn clean package -DskipTests
# Run Spring Boot in background and tail logs to keep container alive
nohup java -jar target/*.war > /var/log/spring-boot.log 2>&1 &
tail -f /var/log/nginx/access.log /var/log/spring-boot.log