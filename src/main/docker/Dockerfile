FROM openjdk:12

LABEL maintainer="admin@vizcentric.com"
LABEL name="Vizcentric"

RUN groupadd -g 999 flairuser && \
    useradd --shell /bin/bash --create-home --home /home/flairuser -r -u 999 -g flairuser flairuser
WORKDIR /app

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="" \
    JHIPSTER_SLEEP=0

EXPOSE 8080
EXPOSE 8081
EXPOSE 8082
EXPOSE 6565

ADD https://github.com/grpc-ecosystem/grpc-health-probe/releases/download/v0.3.0/grpc_health_probe-linux-amd64 /bin/grpc_health_probe
COPY classes/config/ssl/ssl-prod.properties /app/pgsql.properties
COPY ssl/keystore/fbiengine-keystore.p12 /app/keystore.p12
COPY ssl/keystore/fbiengine-truststore.p12 /app/truststore.p12
COPY ssl/certsgen/client.crt /app/client.crt
COPY ssl/certsgen/client.pem /app/client.pem
COPY ssl/certsgen/server.crt /app/certChainFile.crt
COPY ssl/certsgen/server.pem /app/privateKeyFile.pem
COPY ssl/certsgen/ca.crt /app/trustCertCollectionFile.crt
COPY ssl/cachecertsgen/client.crt /app/cacheClientCertChainFile.crt
COPY ssl/cachecertsgen/client.pem /app/cacheClientPrivateKeyFile.pem
COPY ssl/cachecertsgen/ca.crt /app/cacheTrustCertCollectionFile.crt

COPY *.war /app/app.war

RUN chown -R flairuser:flairuser /app
RUN chmod -R 755 /app
RUN chmod +x /bin/grpc_health_probe

USER flairuser
CMD echo "The application will start in ${JHIPSTER_SLEEP}s..." && \
    sleep ${JHIPSTER_SLEEP} && \
    java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app/app.war