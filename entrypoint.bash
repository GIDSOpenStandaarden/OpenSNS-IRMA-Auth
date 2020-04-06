#!/usr/bin/env bash

# see https://vaneyckt.io/posts/safer_bash_scripts_with_set_euxo_pipefail/
set -euxo pipefail

function check_variable {
  eval "VAL=\"\$$1\""
  if [ -z "$VAL" ]; then
    echo "The environment variable $1 is required."
    exit 1
  fi
}

function check_file {
  eval "VAL=\"\$$1\""
  if [ -z "$VAL" ]; then
    echo "The environment variable $1 is required."
    exit 1
  elif [ ! -s "$VAL" ]; then
    echo "The the file $VAL is not found."
    exit 1
  fi
}

check_variable "IRMA_SERVER_URL"
check_variable "IRMA_SERVER_TOKEN"

## Check if there is a value key present in GIDS_SERVER_JWTPRIVATEKEY or GIDS_SERVER_JWTPUBLICKEY.
if [ -z "${GIDS_SERVER_JWTPRIVATEKEY}" ] && [ -z "${GIDS_SERVER_JWTPUBLICKEY}" ]; then
  echo "Generating new keypair"
  export GIDS_SERVER_JWTPUBLICKEY=/keys/gids_public_key.pem
  export GIDS_SERVER_JWTPRIVATEKEY=/keys/gids_private_key.pem
  bash /tools/keygen.sh "${GIDS_SERVER_JWTPUBLICKEY}" "${GIDS_SERVER_JWTPRIVATEKEY}"
fi

if [ -s "${GIDS_SERVER_JWTPUBLICKEY}" ]; then
  # Note that the public key file is only used for logging here, the server does not need one.
  echo "The public key is set to ${GIDS_SERVER_JWTPUBLICKEY}"
  cat "${GIDS_SERVER_JWTPUBLICKEY}"
  echo ""
elif [ ! -z "${GIDS_SERVER_JWTPUBLICKEY}" ]; then
  echo "The public key is set to:"
  echo "${GIDS_SERVER_JWTPUBLICKEY}"
  echo ""
fi

echo "Downloading IRMA publi key from ${IRMA_SERVER_URL}/publickey"
IRMA_SERVER_JWTPUBLICKEY="`wget -qO- ${IRMA_SERVER_URL}/publickey`"
echo "IRMA server public key is:"
echo "${IRMA_SERVER_JWTPUBLICKEY}"
export IRMA_SERVER_JWTPUBLICKEY

java -jar /gids-irma-auth.jar
