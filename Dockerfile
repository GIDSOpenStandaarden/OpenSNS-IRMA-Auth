FROM maven:3.6.3-jdk-11 AS build

ADD pom.xml /

ADD src /src

RUN mvn --quiet clean package

FROM openjdk:11-jre

RUN apk update && apk add bash openssl

COPY --from=build /target/gids-irma-auth.jar /gids-irma-auth.jar

ADD keys /keys

ADD entrypoint.bash /

ADD tools /tools

RUN chmod +x entrypoint.bash

EXPOSE 8080

ENTRYPOINT [ "bash", "entrypoint.bash" ]
