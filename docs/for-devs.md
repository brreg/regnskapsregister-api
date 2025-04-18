# regnskapsregister-API

Key values from annual accounts for the last accounting year as open data.

# Local development

1. Install java, git, maven and postgres (optional)
   - You can also run postgres with Podman: `podman play kube pods/postgres.yaml`
2. Set the application profile to `localhost`. 
   - In IDEA: Application -> Edit Configurations -> Active profiles = `localhost`
3. Set the missing environment variables:
   - In IDEA: Application -> Edit Configurations -> Modify Options -> Environment variables -> add the following:
       ```
       RRAPI_SFTP_USER="<replace-me>";
       RRAPI_SFTP_PASSWORD="<replace-me>";
       ```

    >    See the rest of the environment variables in application.yml


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

## Kopi av årsregnskap
In order to be able to run the endpoints for kopi av årsregnskap locally, you will need to have [GraphicsMagick](http://www.graphicsmagick.org/) installed in order for the application to be able to convert TIFF to PDF.


# Build and deploy on Openshift (BR only)
1. Trigger the pipeline `make trigger-pipeline`
2. If the build succeeds, grab the git hash and paste it into both deployment files in the appconfig.

# Exporting importfiles

Importfiles are generated by Regnskapsregisteret and uploaded to SFTP-server with a name-format "yyyyMMddHHmmss-masse.xml". These files are downloaded and imported by regnskapsregister-api UpdateService daily.
When importing a file, the filename and a zipped version of the file is stored in rregapi.regnskaplog, and all Regnskap in that file is parsed and stored in rregapi.regnskap, together with a foreign key relation to the rregapi.regnskaplog entry.

Sometimes (for testing/debugging?), you might want to fetch the import-file for a given date (postgres will only support exporting a single row at a time. If you want more files, make a wrapper script)
 
## From internal Brreg Postgres
Use a tool like pgAdmin og DataGrip to connect to the appropriate Postgres instance (ST or PPE). Connection info is found in the appconfig repository (https://bitbucket.brreg.no/projects/OPENSHIFT-APPCONFIG/repos/regnskap/browse)

In a pgAdmin SQL view, run (example for 31.dec 2018) the query "SELECT ENCODE(zipfile, 'base64') FROM rregapi.regnskaplog WHERE filename LIKE '20181231%' LIMIT 1;", use File|Export, use "No Quoting" and uncheck "Column Names", and export to a file. (using psql, you can use "\copy" for the SELECT statement to export to file)  

In a shell, execute "base64 --decode <exported_file> > file.zip"  

Unzip this file to get the original masse.xml-file

## Moving data from one database instance to another
Overview of the process:
* Extract encoded import files from regnskaplog table in old database
* Recreate import xml files from the extracted data
* Upload the import files to the sftp server where regnskap-api usually gets its data
* Let the ordinary import do its thing. The data will be imported in the same way as the ordinary database import process.

### Extract encoded import files from old database
Log on to database with psql client. If you are extracting from an google cloud database, you must first install the cloud sql proxy. Follow Googles's instructions here: https://cloud.google.com/sql/docs/postgres/quickstart-proxy-test

You then need to download a key for the database's service account. 
Go to Google Cloud Platform|SQL, select the SQL instance and in Overview, take a note of which Service Account is used.
Go to Google Cloud Platform|IAM & Admin|Service Accounts, find the service account, and create a key using the corresponding three dot Action.

Then run the proxy:
```
./cloud_sql_proxy -instances=<gcp-database-instance-id>:5432 -credential_file=service-account-key-file.json
```

Log on to psql, change to correct database and run extraction query:
```
psql --host=<hostname> -U <databaseuser> -d <databasename>
\copy (select filename, ENCODE(zipfile, 'base64') from rregapi.regnskaplog) to 'export.csv' csv
```

### Extact xml import files from exported csv file
This is done by running a python script: migration/split.py
Edit the file to ensure filenames and directories are correct
```
python split.py
```
### Upload import files to sftp server
Upload the xml files produced in the previous step into the sftp server used by the new regnskap-api instance to fetch new data.
Use your preferred sftp client, and use the same credentials used by the application to access the sftp server.

### Import file processing
The application will normally start processing the files each morning, and it will import all files not already imported (it will check its log).
If you want the import to start immediately, restart the pod on Openshift.


# Useful links
[RREG-API GitHub repository](https://github.com/brreg/regnskapsregister-api/)

## Environments
The application is deployed to two environments on Brønnøysundregistrene's Openshift environment:
* Pre-production environment: PPE
* Production environment: PRD
The application is deployed to a production environment, but it is currently made available as a "preview", with no guarantees of quality of service.

| Environment   | API                                                     |
|---------------|---------------------------------------------------------|
| PPE           | https://data.ppe.brreg.no/regnskapsregisteret/regnskap  |
| PRD           | https://data.brreg.no/regnskapsregisteret/regnskap      |
