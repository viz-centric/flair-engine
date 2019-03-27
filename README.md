[![Build Status](https://dev.azure.com/VizCentric/Flair%20BI/_apis/build/status/viz-centric.flair-engine?branchName=master)](https://dev.azure.com/VizCentric/Flair%20BI/_build/latest?definitionId=8&branchName=master)

# flair-engine
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
