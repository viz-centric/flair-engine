# Flair Engine
Flair Engine is responsible for query parsing and execution


## Release

To perform a release you need:
*  have configured credentials in settings.xml

    ```
    <settings>  
        <servers>  
            <server>
                <id>github-credentials</id>  
                <username>myUser</username>  
                <password>myPassword</password>  
            </server>
            <server>
                <id>docker.io</id>
                <username>dockerhubUsername</username>
                <password>dockerhubPassword</username>
            </server>
        </servers>
    </settings>   
    ```
* run following command you need to set development version and release version:

   ``` 
   mvn release:clean release:prepare release:perform -DreleaseVersion=${releaseVersion} -DdevelopmentVersion=${developmentVersion}
   ```

## Enabling SSL
To enable SSL between flair engine and flair bi, or between flair engine and flair cache, you should generate SSL certificates first.

### Enable SSL between flair engine and flair bi 
To generate SSL certs, run this command:

```bash
cd src/main/resources/ssl/certsgen
```

Make sure you open that bash file and check `SERVER_CN` and `CLIENT_CN` variables there. If you plan to run in docker environment, then keep
these values as is. If you plan to deploy to a real production environment, then change these values to contain real hostnames of the services.

After that, copy the whole contents from `src/main/resources/ssl/certsgen/*` to flair-bi projects to the same location
```bash
cp src/main/resources/ssl/certsgen ../../../../../../flair-bi/src/main/resources/ssl/certsgen
``` 

### Enable SSL between flair engine and flair cache 
To generate SSL certs, run this command:

```bash
cd src/main/resources/ssl/cachecertsgen
```

Make sure you open that bash file and check `SERVER_CN` and `CLIENT_CN` variables there. If you plan to run in docker environment, then keep
these values as is. If you plan to deploy to a real production environment, then change these values to contain real hostnames of the services.

After that, copy the whole contents from `src/main/resources/ssl/cachecertsgen/*` to flair-bi projects to the same location
```bash
cp src/main/resources/ssl/cachecertsgen ../../../../../../flair-cache/src/main/resources/ssl/cachecertsgen
``` 

### Run health check

```bash
grpc_health_probe -addr localhost:6565 \
    -tls \
    -tls-ca-cert=/app/trustCertCollectionFile.crt \
    -tls-client-cert=/app/clientCertChainFile.crt \
    -tls-client-key=/app/clientPrivateKeyFile.pem \
    -tls-server-name=flair-engine-grpc
```