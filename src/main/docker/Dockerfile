FROM openjdk:8

RUN addgroup -S flairgroup && adduser -S flairuser -G flairgroup
USER flairuser
WORKDIR /usr/local/bin/flairuser

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="" \
    JHIPSTER_SLEEP=0

VOLUME /tmp
EXPOSE 8080
EXPOSE 8081
EXPOSE 8082
EXPOSE 6565

COPY classes/config/ssl/ssl-prod.properties /pgsql.properties
COPY ssl/keystore/fbiengine-keystore.p12 /keystore.p12
COPY ssl/keystore/fbiengine-truststore.p12 /truststore.p12
COPY ssl/cert/fbiengine/fbiengine.pem /server.crt
COPY ssl/cert/fbiengine/fbiengine-pkcs8.key /server.key

COPY *.war /app.war

CMD echo "The application will start in ${JHIPSTER_SLEEP}s..." && \
    sleep ${JHIPSTER_SLEEP} && \
    java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.war