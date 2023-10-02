## Disclaimer: This Dockerfile is for internal use at BR.

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
# Default builder image is Java 17
FROM quay.apps.ocp-svc.base.brreg.no/base-selvbetjening/builder-java as builder

# To use Java 8 instead, comment out the line above,
# and uncomment the following line:
#FROM quay.apps.ocp-svc.base.brreg.no/base-selvbetjening/builder-java8 as builder

# To use Java 11 instead, comment out the line above,
# and uncomment the following line:
#FROM quay.apps.ocp-svc.base.brreg.no/base-selvbetjening/builder-java11 as builder


COPY pom.xml .

# cache dependencies in early layer
RUN mvn dependency:resolve --batch-mode --no-transfer-progress --quiet

# Copy all files, expect those ignored in .dockerignore
COPY . .

RUN mvn install -Dmaven.test.skip=true --batch-mode --no-transfer-progress

# image which production app should run in. Default is Openjdk 17
FROM quay.apps.ocp-svc.base.brreg.no/base-container/openjdk17

# To use Openjdk 8 instead, comment out the line above,
# and uncomment the following line:
#FROM quay.apps.ocp-svc.base.brreg.no/base-container/openjdk8

# To use Openjdk 11 instead, comment out the line above,
# and uncomment the following line:
#FROM quay.apps.ocp-svc.base.brreg.no/base-container/openjdk11

# Configuration for Elastic APM agent and ECS logging format
# Correct logback.xml is added in parent image
ENV JAVA_OPTS="-javaagent:/deployments/apm/elastic-apm-agent.jar -Dlogging.config=/deployments/logback.xml"

## copy build results from builder. This is for Java 17 and Java 11
COPY --from=builder /home/default/target/*.jar /deployments

# To use Java 8 instead, comment out the line above,
# and uncomment the following line:
#COPY --from=builder /home/jboss/target/*.jar /deployments