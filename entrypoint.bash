#!/usr/bin/env bash

# see https://vaneyckt.io/posts/safer_bash_scripts_with_set_euxo_pipefail/
set -euxo pipefail

echo "IRMA_SERVER_URL is ${IRMA_SERVER_URL}"
echo "IRMA_SERVER_TOKEN is ${IRMA_SERVER_TOKEN}"

if [ -s "${GIDS_SERVER_JWTPUBLICKEY}" ]; then
  # Note that the public key file is only used for logging here, the server does not need one.
  echo "The public key is set to file ${GIDS_SERVER_JWTPUBLICKEY}"
  cat "${GIDS_SERVER_JWTPUBLICKEY}"
  echo ""
elif [ ! -z "${GIDS_SERVER_JWTPUBLICKEY}" ]; then
  echo "The public key is set to:"
  echo "${GIDS_SERVER_JWTPUBLICKEY}"
  echo ""
fi

echo "Downloading IRMA public key from ${IRMA_SERVER_URL}/publickey"
IRMA_SERVER_JWTPUBLICKEY="`wget --no-check-certificate -qO- ${IRMA_SERVER_URL}/publickey`"
echo "IRMA server public key is:"
echo "${IRMA_SERVER_JWTPUBLICKEY}"
export IRMA_SERVER_JWTPUBLICKEY

java -jar /gids-irma-auth.jar
