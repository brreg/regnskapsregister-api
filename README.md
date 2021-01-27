# regnskapsregister-API

Key values from annual accounts for the last accounting year as open data.

# Local development

## Install java, git, maven and postgres

##### Linux
```
sudo apt update
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install default-jdk git maven postgresql
```

##### Windows
Git for Windows - https://gitforwindows.org/

Apache Maven - https://maven.apache.org/download.cgi

PostgreSQL - https://www.postgresql.org/download/windows/


## Environment variables
These are needed for RREG-API integration:
```
RRAPI_POSTGRES_DB_URL="jdbc:postgresql://localhost:5432/postgres?currentSchema=rreg&sslmode=prefer"
RRAPI_POSTGRES_DBO_USER="postgres" (whatever you used in your locally installed Postgresql)
RRAPI_POSTGRES_DBO_PASSWORD="password" (whatever you used in your locally installed Postgresql)
RRAPI_POSTGRES_USER="postgres" (whatever you used in your locally installed Postgresql)
RRAPI_POSTGRES_PASSWORD="password" (whatever you used in your locally installed Postgresql)
RRAPI_SFTP_SERVER="filporten.brreg.no"
RRAPI_SFTP_PORT="22"
RRAPI_SFTP_USER="RRmasse"
RRAPI_SFTP_PASSWORD="<password>"
RRAPI_SFTP_DIRECTORY="/out"
RRAPI_SLACK_TOKEN="disabled"
```

In addition, you can add this to the application.properties file:
regnskap.fileimport.directory=C:/src/regnskapsregister-api/ (or wherever you might have RREG masse.xml-files)


##### Linux
Open ~/.bashrc and add the lines
```
export RRAPI_POSTGRES_DB_URL="jdbc:postgresql://localhost:5432/postgres?currentSchema=rreg&sslmode=prefer"
export RRAPI_POSTGRES_DBO_USER="postgres" (whatever you used in your locally installed Postgresql)
...
```
Update from ~/.bashrc with
```
source ~/.bashrc
```

Check that they have been added with "printenv"

##### Windows
“Advanced system settings” → “Environment Variables”


## Clone and run
```
git clone {repo}
mvn clean install
(run or debug Application.main)
```
Liquibase will create database schema "rreg" and the database itself at startup. If you have set the correct SFTP username and password, or have set a local file import directory, RREG masse.xml-files will be downloaded and imported.   

## Test that everything is running
"/src/main/resources/specification/examples/RegnskapsAPI.postman_collection.json"

Import this collection in Postman to test the api locally.


# Official builds

When something is pushed to the branch "ut1", [GitHub Actions](https://github.com/brreg/regnskapsregister-api/actions) will automatically build and deploy to the official ut1 environment.
Likewise, when something is pushed to the branch "prod", GitHub Actions will automatically build and deploy to the official prod environment.
(The environments are defined in the [RREG-API Helm Chart](https://github.com/brreg/helm-chart/tree/main/helm-chart-sources/regnskapsregister-api))


## Useful links
[RREG-API GitHub repository](https://github.com/brreg/regnskapsregister-api/)

[RREG-API Helm Chart](https://github.com/brreg/helm-chart/tree/main/helm-chart-sources/regnskapsregister-api)

### UT1

[RREG-API GCP deployment details](https://console.cloud.google.com/kubernetes/deployment/europe-north1-a/rreg-dev/ut1/regnskapsregister-api/overview)

[RREG-API Endpoint](http://rreg.ut1.rreg-dev.brreg.no/regnskap)

[RREG-API Swagger UI](http://rreg.ut1.rreg-dev.brreg.no/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config)

### Prod

[RREG-API PROD GCP deployment details](https://console.cloud.google.com/kubernetes/deployment/europe-north1-a/rreg-prod/prod/regnskapsregister-api/overview)

[RREG-API Endpoint](http://34.98.91.231/regnskap)
