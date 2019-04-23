FROM openjdk:8

LABEL maintainer="admin@vizcentric.com"
LABEL name="Vizcentric"

RUN groupadd -g 999 flairuser && \
    useradd --shell /bin/bash --create-home --home /home/flairuser -r -u 999 -g flairuser flairuser
WORKDIR /app

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="" \
    JHIPSTER_SLEEP=0

VOLUME /app/tmp
EXPOSE 8080
EXPOSE 8081
EXPOSE 8082
EXPOSE 6565

COPY classes/config/ssl/ssl-prod.properties /app/pgsql.properties
COPY ssl/keystore/fbiengine-keystore.p12 /app/keystore.p12
COPY ssl/keystore/fbiengine-truststore.p12 /app/truststore.p12
COPY ssl/cert/fbiengine/fbiengine.pem /app/server.crt
COPY ssl/cert/fbiengine/fbiengine-pkcs8.key /app/server.key

COPY *.war /app/app.war

RUN chown -R flairuser:flairuser /app
RUN chown -R flairuser:flairuser /app/*
RUN chmod -R 755 /app

USER flairuser
CMD echo "The application will start in ${JHIPSTER_SLEEP}s..." && \
    sleep ${JHIPSTER_SLEEP} && \
    java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app/app.war