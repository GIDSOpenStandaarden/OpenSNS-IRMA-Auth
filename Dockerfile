FROM openjdk:8u181-jre
ENV TZ="Europe/Amsterdam"

ADD target/gids-irma-auth.jar /gids-irma-auth.jar

ADD keys /keys

ADD entrypoint.bash /

ADD tools /tools

RUN chmod +x entrypoint.bash

EXPOSE 8080

ENV GIDS_SERVER_JWTPUBLICKEYFILE=/keys/gids_public_key.pem
ENV GIDS_SERVER_JWTPRIVATEKEYFILE=/keys/gids_private_key.pem

ENTRYPOINT ["bash", "entrypoint.bash" ]
