FROM openjdk:8u181-jre
ENV TZ="Europe/Amsterdam"

ADD target/gids-irma-auth.jar /gids-irma-auth.jar

ADD keys /keys

ADD entrypoint.bash /

ADD tools /tools

RUN chmod +x entrypoint.bash

EXPOSE 8080

ENTRYPOINT [ "bash", "entrypoint.bash" ]
