#!/bin/sh
docker kill $(docker ps -q)
docker rm $(docker ps -aq)
docker volume ls -qf dangling=true | xargs -r docker volume rm
docker rmi -f $(docker images -q)