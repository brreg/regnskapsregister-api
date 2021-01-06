FROM maven:3.6.3-openjdk-14 AS build
RUN mkdir /app
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress -DskipTests clean package

FROM openjdk:11-jre-slim
ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mkdir /app
RUN addgroup --gid 1001 --system app && \
  adduser --uid 1001 --system app --gid 1001 && \
  chown -R app:app /app && \
  chmod 770 /app
USER app:app
WORKDIR /app
COPY --chown=app:app --from=build /app/target/app.jar ./
CMD java -jar app.jar
