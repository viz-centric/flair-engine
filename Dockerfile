FROM java:8-jre-alpine
LABEL maintainer Manoj<Manoj.Horcrux@gmail.com>

COPY src/main/resources/ssl/keystore/fbiengine-keystore.p12 /var/fbiengine-keystore.p12
COPY src/main/resources/ssl/grpc/cloud/server.crt /var/server.crt
COPY src/main/resources/ssl/grpc/cloud/server.pem /var/server.pem
COPY src/main/resources/ssl/keystore/fbiengine-truststore.p12 /var/fbiengine-truststore.p12
COPY target/*.war /var/app.war
COPY Docker/button.sh /var/button.sh

RUN chmod +x /var/button.sh
WORKDIR /var

EXPOSE 8080 6565

CMD [ "/bin/ash", "button.sh" ]