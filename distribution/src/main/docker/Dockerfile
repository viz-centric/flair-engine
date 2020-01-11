FROM openjdk:12 AS builder
RUN yum -y update && \
	yum -y install zip && \
	yum -y install unzip
	
	
COPY *.zip /distr.zip
RUN  unzip distr.zip \
	 && mv flair-engine-* dist \
	 && ls dist \
	 && rm distr.zip



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
COPY --from=builder /dist/ssl/ssl-prod.properties /app/pgsql.properties
COPY --from=builder /dist/ssl/keystore/fbiengine-keystore.p12 /app/keystore.p12
COPY --from=builder /dist/ssl/keystore/fbiengine-truststore.p12 /app/truststore.p12
COPY --from=builder /dist/ssl/certsgen/client.crt /app/clientCertChainFile.crt
COPY --from=builder /dist/ssl/certsgen/client.pem /app/clientPrivateKeyFile.pem
COPY --from=builder /dist/ssl/certsgen/server.crt /app/certChainFile.crt
COPY --from=builder /dist/ssl/certsgen/server.pem /app/privateKeyFile.pem
COPY --from=builder /dist/ssl/certsgen/ca.crt /app/trustCertCollectionFile.crt
COPY --from=builder /dist/ssl/cachecertsgen/client.crt /app/cacheClientCertChainFile.crt
COPY --from=builder /dist/ssl/cachecertsgen/client.pem /app/cacheClientPrivateKeyFile.pem
COPY --from=builder /dist/ssl/cachecertsgen/ca.crt /app/cacheTrustCertCollectionFile.crt

COPY --from=builder /dist/plugins/ /app/plugins
COPY --from=builder /dist/*.war /app/app.war

RUN chown -R flairuser:flairuser /app
RUN chmod -R 755 /app
RUN chmod +x /bin/grpc_health_probe

USER flairuser
CMD echo "The application will start in ${JHIPSTER_SLEEP}s..." && \
    sleep ${JHIPSTER_SLEEP} && \
    java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app/app.war