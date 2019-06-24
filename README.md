# regnskapsregister-API

Key values from annual accounts for the last accounting year as open data.

# Local development

## Install java, git, maven, docker and docker-compose

##### Linux
```
sudo apt update
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install default-jdk git maven docker.io docker-compose
```

##### Windows
Git for Windows - https://gitforwindows.org/

Apache Maven - http://maven.apache.org/download.cgi

Docker for Windows - https://hub.docker.com/editions/community/docker-ce-desktop-windows

## Steps only necessary for Linux

##### Configure Docker to start on Boot
```
systemctl start docker
systemctl enable docker
```

##### Enable executing Docker and Maven without sudo
```
sudo adduser ${USER} docker
sudo adduser ${USER} mvn
```

Check that they have been added with "id -nG", force the update with a reboot or with "su - ${USER}"

## Environment variables
These are needed to connect to the local database
```
RRAPI_MONGO_USERNAME="rr_db"
RRAPI_MONGO_PASSWORD="Passw0rd"
```

##### Linux
Open ~/.bashrc and add the lines
```
export RRAPI_MONGO_USERNAME="rr_db"
export RRAPI_MONGO_PASSWORD="Passw0rd"
export RRAPI_SFTP_SERVER="rr_sftp_host"
export RRAPI_SFTP_USER="rr_sftp_user"
export RRAPI_SFTP_PASSWORD="rr_sftp_pass"
export RRAPI_SFTP_PORT="rr_sftp_port"
export RRAPI_SFTP_DIRECTORY="rr_sftp_dir"
```
Update from ~/.bashrc with
```
source ~/.bashrc
```

Check that they have been added with "printenv"

##### Windows
“Advanced system settings” → “Environment Variables”

## Nice to have
#### Postman
https://www.getpostman.com/

#### MongoDB
https://docs.mongodb.com/manual/installation

## Clone and run
```
git clone {repo}
mvn clean install
docker-compose up -d
```
-d enables "detached mode"

## Test that everything is running
"/src/main/resources/specification/examples/RegnskapsAPI.postman_collection.json"

Import this collection in Postman to test the api locally.