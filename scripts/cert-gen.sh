#!/usr/bin/env bash
set -e


PROJECT_DIR=./../src/main/resources/ssl
CERT_DIR=${PROJECT_DIR}/cert
KEY_LENGTH=2086
KEYSTORE_DIR=${PROJECT_DIR}/keystore
DEFAULT_PASSPHRASE=changeit
EXT_PATH=${PROJECT_DIR}/ext.conf


function generate_key(){
    # $1 KEY PATH
    # $2 PASSPPRAHSE
    # $3 KEYLENGTH
    echo "Generating $1 key"
    openssl genrsa \
            -out $1 \
            -passout pass:$2 $3
}

function generate_csr(){
    # $1 SUBJECT NAME
    # $2 KEY PATH
    # $3 CSR PATH
    echo "Generating Csr with CN= $1"
    openssl req \
            -new \
            -key $2 \
            -out $3 \
            -subj "//CN=$1"
}

function sign_certificate(){
    # $1 CSR PATH
    # $2 CERT PATH
    # $3 CA CERT PATH
    # $4 CA KEY PATH
    # $5 CONF FILE PATH
    # $6 EXT NAME IN CONFIG
    echo "Generate $6 signed by root ca"
    openssl x509 -req \
            -extensions $6 \
            -extfile $5 \
            -in $1 \
            -CA $3 -CAkey $4 \
            -CAcreateserial \
            -out $2 \
            -days 3650
}

function generate_keystore(){
    # $1 KEYSTORE PATH
    # $2 CERT PATH
    # $3 KEY PATH
    # $4 ENTRY NAME IN KEY STORE
    # $5 KEYSTORE PASSPHRASE
    echo "Creating $4 keystore"

    openssl pkcs12 \
        -export \
        -in $2 \
        -inkey $3 \
        -out $1 \
        -name "$4" \
        -passout pass:$5
}

function generate_truststore(){
    # $1 CA CERT PATH
    # $2 TRUSTSTORE PATH
    # $3 passphrase

    ### create truststore
    openssl pkcs12 \
            -export \
            -nokeys \
            -in $1\
            -out $2 \
            -passout pass:$3

    ## import
    keytool -import \
            -alias ca \
            -file $1 \
            -keystore $2 \
            -noprompt \
            -storepass $3
}



###################################################################
#
#                           Root CA
#
###################################################################

function generate_root_ca(){
    CA_DIR=${CERT_DIR}/ca
    CA_KEY=${CA_DIR}/ca.key
    CA_CRT=${CA_DIR}/ca.pem

    generate_key ${CA_KEY} ${DEFAULT_PASSPHRASE} ${KEY_LENGTH}

    echo "Generate root Ca certificate"
    openssl req -x509 \
      -new \
      -nodes \
      -key ${CA_KEY} \
      -days 3650 \
      -out ${CA_CRT} \
      -subj "//CN=FBI ROOT CA"
}



###################################################################
#
#                           FBI ENGINE
#
###################################################################

function generate_flair_engine(){
    ENG_DIR=${CERT_DIR}/fbiengine
    ENG_KEY=${ENG_DIR}/fbiengine.key
    ENG_CSR=${ENG_DIR}/fbiengine.csr
    ENG_CRT=${ENG_DIR}/fbiengine.pem
    ENG_PKCS12_KEYSTORE=${KEYSTORE_DIR}/fbiengine-keystore.p12
    ENG_PCKS12_TRUSTSTORE=${KEYSTORE_DIR}/fbiengine-truststore.p12

    generate_key ${ENG_KEY} ${DEFAULT_PASSPHRASE} ${KEY_LENGTH}

    openssl pkcs8 -topk8 -nocrypt -in ${ENG_KEY} -out pkcs8-${ENG_KEY}

    generate_csr "fbiengine" ${ENG_KEY} ${ENG_CSR}

    sign_certificate ${ENG_CSR} ${ENG_CRT} ${CA_CRT} ${CA_KEY} ${EXT_PATH} fbiengine

    ### create fbiengine keystore pkcs12 format
    generate_keystore ${ENG_PKCS12_KEYSTORE} ${ENG_CRT} ${ENG_KEY} "fbiengine" ${DEFAULT_PASSPHRASE}

    ### create truststore
    generate_truststore ${CA_CRT} ${ENG_PCKS12_TRUSTSTORE} ${DEFAULT_PASSPHRASE}
}


###################################################################
#
#                           FlairBI
#
###################################################################

function generate_flair_bi(){
    FBI_DIR=${CERT_DIR}/flairbi
    FBI_KEY=${FBI_DIR}/flairbi.key
    FBI_CSR=${FBI_DIR}/flairbi.csr
    FBI_CRT=${FBI_DIR}/flairbi.pem
    FBI_PKCS12_KEYSTORE=${KEYSTORE_DIR}/flairbi-keystore.p12
    FBI_PKCS12_TRUSTSTORE=${KEYSTORE_DIR}/flairbi-truststore.p12

    ### generate flairbi private key
    generate_key ${FBI_KEY} ${DEFAULT_PASSPHRASE} ${KEY_LENGTH}

    ### generate flairbi signing request
    generate_csr "flairbi" ${FBI_KEY} ${FBI_CSR}

    ### generate flairbi certificate signed by root ca
    sign_certificate ${FBI_CSR} ${FBI_CRT} ${CA_CRT} ${CA_KEY} ${EXT_PATH} fbiengine

    ### create flairbi keystore pkcs12 format
    generate_keystore ${FBI_PKCS12_KEYSTORE} ${FBI_CRT} ${FBI_KEY} "flairbi" ${DEFAULT_PASSPHRASE}

    ### create truststore
    generate_truststore ${CA_CRT} ${FBI_PKCS12_TRUSTSTORE} ${DEFAULT_PASSPHRASE}
}


###################################################################
#
#                           FlairBI - PostgreSQL
#
###################################################################

function generate_flair_bi_pgsql(){
    FBI_PSQL_DIR=${CERT_DIR}/flairbi-psql
    FBI_PSQL_KEY=${FBI_PSQL_DIR}/flairbi-psql.key
    FBI_PSQL_CSR=${FBI_PSQL_DIR}/flairbi-psql.csr
    FBI_PSQL_CRT=${FBI_PSQL_DIR}/flairbi-psql.pem

    ### generate flairbi psql private key
    generate_key ${FBI_PSQL_KEY} ${DEFAULT_PASSPHRASE} ${KEY_LENGTH}

    ### generate flairbi psql signing request
    generate_csr "flairbi-psql" ${FBI_PSQL_KEY} ${FBI_PSQL_CSR}

    ### generate flairbi psql certificate signed by root ca
    sign_certificate ${FBI_PSQL_CSR} ${FBI_PSQL_CRT} ${CA_CRT} ${CA_KEY} ${EXT_PATH} flairbi-psql
}


###################################################################
#
#                           FBIENGINE - PostgreSQL
#
###################################################################

function flair_engine_pgsql(){
    ENG_PSQL_DIR=${CERT_DIR}/fbiengine-psql
    ENG_PSQL_KEY=${ENG_PSQL_DIR}/fbiengine-psql.key
    ENG_PSQL_CSR=${ENG_PSQL_DIR}/fbiengine-psql.csr
    ENG_PSQL_CRT=${ENG_PSQL_DIR}/fbiengine-psql.pem

    ### generate fbiengine psql private key
    generate_key ${ENG_PSQL_KEY} ${DEFAULT_PASSPHRASE} ${KEY_LENGTH}

    ### generate fbiengine psql signing request
    generate_csr "fbiengine-psql" ${ENG_PSQL_KEY} ${ENG_PSQL_CSR}

    ### generate fbiengine psql certificate signed by root ca
    sign_certificate ${ENG_PSQL_CSR} ${ENG_PSQL_CRT} ${CA_CRT} ${CA_KEY} ${EXT_PATH} fbiengine-psql
}


###################################################################
#
#                           FlairBI - CouchDB
#
###################################################################
COUCHDB_DIR=${CERT_DIR}/flairbi-couchdb
COUCHDB_KEY=${COUCHDB_DIR}/flairbi-couchdb.key
COUCHDB_CSR=${COUCHDB_DIR}/flairbi-couchdb.csr
COUCHDB_CRT=${COUCHDB_DIR}/flairbi-couchdb.pem
function flair_couch(){
    ### generate couchdb private key
    generate_key ${COUCHDB_KEY} ${DEFAULT_PASSPHRASE} ${KEY_LENGTH}

    ### generate couchdb signing request
    generate_csr "flair-couchdb" ${COUCHDB_KEY} ${COUCHDB_CSR}

    ### generate couchdb certificate signed by root ca
    sign_certificate ${COUCHDB_CSR} ${COUCHDB_CRT} ${CA_CRT} ${CA_KEY} ${EXT_PATH} flair-couchdb
}



###################################################################
#
#                           Flair traefik
#
###################################################################
TRAEFIK_DIR=${CERT_DIR}/flair-traefik
TRAEFIK_KEY=${TRAEFIK_DIR}/flair-traefik.key
TRAEFIK_CSR=${TRAEFIK_DIR}/flair-traefik.csr
TRAEFIK_CRT=${TRAEFIK_DIR}/
function flair_traefik(){


}

