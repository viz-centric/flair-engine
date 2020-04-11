FROM postgres:9.6

ENV TLS_DIR /etc/pki/tls

## Copy all SSL materials and change owner to postgres:postgres and chmod to 0600
COPY ./ca.pem ${TLS_DIR}/certs/ca.pem

RUN chown -R postgres:postgres ${TLS_DIR}/certs/ca.pem \
    && chmod 0600 ${TLS_DIR}/certs/ca.pem

COPY ./fbiengine-psql.key ${TLS_DIR}/private/fbiengine-psql.key

RUN chown -R postgres:postgres ${TLS_DIR}/private/fbiengine-psql.key \
    && chmod 0600 ${TLS_DIR}/private/fbiengine-psql.key


COPY ./fbiengine-psql.pem ${TLS_DIR}/certs/fbiengine-psql.pem

RUN chown -R postgres:postgres ${TLS_DIR}/certs/fbiengine-psql.pem \
    && chmod 0600 ${TLS_DIR}/certs/fbiengine-psql.pem
