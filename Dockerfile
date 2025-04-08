## BUILDER PATTERN
# This Dockerfile uses the builder pattern. If you do not need
# a build before running the application, you can remove the
# builder part, using only a single
#
#   FROM registry.access.redhat.com/ubi8/openjdk-17:latest
#
## CACHING
# Steps in this Dockerfile is sorted by cost and change frequency,
# such that docker can cache layers efficiently. Normally one
# Dockerfile line = one docker image layer.
#
# For example, say you only change code. Then image can be rebuilt
# by running from step:
#
#   COPY . .
#
# in builder image. Similar in production image from step:
#
#   COPY --from=builder /home/jboss/target/*.jar /deployments
#
# which should speed up image build. When building, you should see log
# similar to
#
#   --> Using cache ff21c22c44cccb63deeac799b0aa03fa8cb95f92554a56600bd09f2d9eecd8bc
#

FROM quay.base.brreg.no/brreg_base-container/ubi9-openjdk-21 as builder

COPY pom.xml .

# cache dependencies in early layer
RUN mvn dependency:resolve --batch-mode --no-transfer-progress --quiet

# Copy all files, expect those ignored in .dockerignore
COPY . .

RUN mvn install -Dmaven.test.skip=true --batch-mode --no-transfer-progress


FROM registry.access.redhat.com/ubi9/openjdk-21-runtime

ARG QUAY_EXPIRY="104w"
LABEL quay.expires-after=$QUAY_EXPIRY

ENV TZ=Europe/Oslo
#ENV JAVA_OPTS="-javaagent:/deployments/apm/elastic-apm-agent.jar -Dlogging.config=/deployments/logback.xml -Dnetworkaddress.cache.ttl=30"
ENV _JAVA_OPTIONS="-XX:MaxRAMPercentage=75 -XX:MinRAMPercentage=25"

USER 0

RUN microdnf -y update
RUN microdnf install dnf -y
RUN dnf install https://mirror.stream.centos.org/9-stream/AppStream/x86_64/os/Packages/libwmf-lite-0.2.12-9.el9.x86_64.rpm -y
RUN dnf install https://dl.fedoraproject.org/pub/epel/epel-release-latest-9.noarch.rpm -y
RUN dnf install GraphicsMagick -y

RUN rpm -e --nodeps $(rpm -qa '*rpm*' '*dnf*' '*libsolv*' '*hawkey*' 'yum*')

USER default
COPY src /deployments

# Overstyring av default expiry. If you want a different default that 2 years,
# change the ARG below for how long a tag is kept on the master branch.
# If you want to change how long feature branch images are kept (default 14 days),
# then that can be done by setting a variable in your pipeline/templates/pipeline.yaml task named: docker-image

# Configuration for Elastic APM agent and ECS logging format
# Correct logback.xml is added in parent image

#ENV JAVA_OPTS="-javaagent:/deployments/apm/elastic-apm-agent.jar -Dlogging.config=/deployments/logback.xml"

## copy build results from builder. This is for Java 17 and Java 11
COPY --from=builder /home/default/target/*.jar /deployments

# To use Java 8 instead, comment out the line above,
# and uncomment the following line:
#COPY --from=builder /home/jboss/target/*.jar /deployments
