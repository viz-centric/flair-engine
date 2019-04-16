FROM openjdk:8-jre-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="" \
    JHIPSTER_SLEEP=0

VOLUME /tmp
EXPOSE 8080
EXPOSE 8081
EXPOSE 8082
EXPOSE 6565

CMD echo "The application will start in ${JHIPSTER_SLEEP}s..." && \
    sleep ${JHIPSTER_SLEEP} && \
    java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.war

ADD classes/config/ssl/ssl-prod.properties /pgsql.properties
ADD ssl/keystore/fbiengine-keystore.p12 /keystore.p12
ADD ssl/keystore/fbiengine-truststore.p12 /truststore.p12
ADD ssl/cert/fbiengine/fbiengine.pem /server.crt
ADD ssl/cert/fbiengine/fbiengine-pkcs8.key /server.key

# add directly the war
ADD *.war /app.war
