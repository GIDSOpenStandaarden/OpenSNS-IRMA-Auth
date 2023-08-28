FROM maven:3.8.7-openjdk-18-slim AS build

ADD pom.xml /

ADD src /src

RUN mvn --quiet clean package

FROM openjdk:22-ea-jdk-slim-bullseye

COPY --from=build /target/gids-irma-auth.jar /gids-irma-auth.jar

ADD keys /keys

ADD entrypoint.bash /

ADD tools /tools

RUN chmod +x entrypoint.bash

EXPOSE 8080

ENTRYPOINT [ "/bin/bash", "entrypoint.bash" ]
