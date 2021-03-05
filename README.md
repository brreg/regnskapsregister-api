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
RRAPI_POSTGRES_DB_URL="jdbc:postgresql://localhost:5432/postgres?currentSchema=rregapi&sslmode=prefer"
RRAPI_POSTGRES_DBO_USER="postgres" (whatever you used in your locally installed Postgresql)
RRAPI_POSTGRES_DBO_PASSWORD="password" (whatever you used in your locally installed Postgresql)
RRAPI_POSTGRES_USER="postgres" (whatever you used in your locally installed Postgresql)
RRAPI_POSTGRES_PASSWORD="password" (whatever you used in your locally installed Postgresql)
RRAPI_SFTP_SERVER="filporten.brreg.no"
RRAPI_SFTP_PORT="22"
RRAPI_SFTP_USER="RRmasse"
RRAPI_SFTP_PASSWORD="<password>"
RRAPI_SFTP_DIRECTORY="/out"
RRAPI_SLACK_CHANNEL="prod-error"
RRAPI_SLACK_TOKEN="disabled"
RRAPI_IP_SALT="salt"
```

In addition, you can add this to the application.properties file:
regnskap.fileimport.directory=C:/src/regnskapsregister-api/ (or wherever you might have RREG masse.xml-files)


##### Linux
Open ~/.bashrc and add the lines
```
export RRAPI_POSTGRES_DB_URL="jdbc:postgresql://localhost:5432/postgres?currentSchema=rregapi&sslmode=prefer"
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
Liquibase will create database schema "rregapi" and the database itself at startup. If you have set the correct SFTP username and password, or have set a local file import directory, RREG masse.xml-files will be downloaded and imported.   

## Test that everything is running
"/src/main/resources/specification/examples/RegnskapsAPI.postman_collection.json"

Import this collection in Postman to test the api locally.


# Official builds

When something is pushed to the branch "ut1", [GitHub Actions](https://github.com/brreg/regnskapsregister-api/actions) will automatically build and deploy to the official ut1 environment.
Likewise, when something is pushed to the branch "prod", GitHub Actions will automatically build and deploy to the official prod environment.
(The environments are defined in the [RREG-API Helm Chart](https://github.com/brreg/helm-chart/tree/main/helm-chart-sources/regnskapsregister-api))


# Exporting importfiles

Importfiles are generated by Regnskapsregisteret and uploaded to SFTP-server with a name-format "yyyyMMddHHmmss-masse.xml". These files are downloaded and imported by regnskapsregister-api UpdateService daily.
When importing a file, the filename and a zipped version of the file is stored in rregapi.regnskaplog, and all Regnskap in that file is parsed and stored in rregapi.regnskap, together with a foreign key relation to the rregapi.regnskaplog entry.
Sometimes (for testing/debugging?), you might want to fetch the import-file for a given date (postgres will only support exporting a single row at a time. If you want more files, make a wrapper script)

First of all: If you have a local install of Postgres running on port 5432, stop it!

Download [cloud_sql_proxy](https://github.com/GoogleCloudPlatform/cloudsql-proxy/releases) and put it somewhere in PATH.

Go to Google Cloud Platform|SQL, select the SQL instance and in Overview, take a note of which Service Account is used.  
Go to Google Cloud Platform|IAM & Admin|Service Accounts, find the service account, and create a key using the corresponding three dot Action.  
In a shell, execute "cloud_sql_proxy_x64 -instances=rreg-prod:europe-north1:rreg-sql11-prod=tcp:5432 -credential_file=<key.json>" (adjust for OS platform and rreg-api project)  
Use a tool like pgAdmin (I have seen problems using pgAdmin 4. Using pgAdmin 3 and just ignoring startup error dialogs works great!), connect to localhost:5432. Database should match the one in Google Cloud Platform|SQL|Databases. Username/password provided on need-to-know from someone that knows.  
In a pgAdmin SQL view, run (example for 31.dec 2018) the query "SELECT ENCODE(zipfile, 'base64') FROM rregapi.regnskaplog WHERE filename LIKE '20181231%' LIMIT 1;", use File|Export, use "No Quoting" and uncheck "Column Names", and export to a file. (using psql, you can use "\copy" for the SELECT statement to export to file)  
In a shell, execute "base64 --decode <exported_file> > file.zip"  
Unzip this file to get the original masse.xml-file

Exit pgAdmin and abort cloud_sql_proxy_x64 when done.
  

# Useful links
[RREG-API GitHub repository](https://github.com/brreg/regnskapsregister-api/)

[RREG-API Helm Chart](https://github.com/brreg/helm-chart/tree/main/helm-chart-sources/regnskapsregister-api)

## UT1

[RREG-API GCP deployment details](https://console.cloud.google.com/kubernetes/deployment/europe-north1-a/rreg-dev/ut1/regnskapsregister-api/overview)

[RREG-API Endpoint](http://rreg.ut1.rreg-dev.brreg.no/regnskap)

[RREG-API Swagger UI](http://rreg.ut1.rreg-dev.brreg.no/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config)

## Prod

[RREG-API PROD GCP deployment details](https://console.cloud.google.com/kubernetes/deployment/europe-north1-a/rreg-prod/prod/regnskapsregister-api/overview)

[RREG-API Endpoint](http://34.98.91.231/regnskap)
