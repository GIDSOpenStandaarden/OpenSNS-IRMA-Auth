FROM maven:3.6-jdk-8 AS build

ADD pom.xml /

ADD src /src

RUN mvn clean install

FROM openjdk:8u181-jre-alpine

RUN apk update && apk add bash openssl

COPY --from=build /target/gids-irma-auth.jar /gids-irma-auth.jar

ENV TZ="Europe/Amsterdam"

ADD keys /keys

ADD entrypoint.bash /

ADD tools /tools

RUN chmod +x entrypoint.bash

EXPOSE 8080

ENTRYPOINT [ "/bin/bash", "entrypoint.bash" ]
