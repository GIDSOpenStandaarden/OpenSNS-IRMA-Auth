#!/usr/bin/env bash


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

## Check if there is a private key file added to the container and set in GIDS_SERVER_JWTPRIVATEKEYFILE.
if [ ! -s "$GIDS_SERVER_JWTPRIVATEKEYFILE" ] && [ -z "$GIDS_SERVER_JWTPRIVATEKEY" ]; then
  echo "Generating keypair becasue both envorionment variables GIDS_SERVER_JWTPRIVATEKEY and GIDS_SERVER_JWTPRIVATEKEYFILE are not set"
  bash /tools/keygen.sh "$GIDS_SERVER_JWTPUBLICKEYFILE" "$GIDS_SERVER_JWTPRIVATEKEYFILE"
fi

if [ -s "$GIDS_SERVER_JWTPUBLICKEYFILE" ]; then
  # Note that the public key file is only used for logging here, the server does not need one.
  echo "The public key is set to:"
  cat "$GIDS_SERVER_JWTPUBLICKEYFILE"
  echo ""
fi




java -jar /gids-irma-auth.jar
