#!/bin/bash

# Check if openssl exists
if ! type openssl >/dev/null 2>&1; then
    >&2 echo "openssl not found, exiting"
    exit 1
fi

# Read the parameters from the input
if [[ $# -eq 0 ]]; then
	PRIVATE_KEY_FILE=gids_private_key.pem
	PUBLIC_KEY_FILE=gids_public_key.pem
fi
if [[ $# -eq 1 ]]; then
	>&2 echo "Either supply 0 or 2 arguments"
	exit 1
fi
if [[ $# -eq 2 ]]; then
	PUBLIC_KEY_FILE=$1
	PRIVATE_KEY_FILE=$2
fi

echo "Public file is $PUBLIC_KEY_FILE, private key file is $PRIVATE_KEY_FILE"

# Check for a private key file
if [ -s "$PRIVATE_KEY_FILE" ]; then
  echo "Private key: $PRIVATE_KEY_FILE  exist, not generating a new one"
else
  # Generate a private key in PEM format
  openssl genpkey -algorithm RSA -out "$PRIVATE_KEY_FILE.tmp" -pkeyopt rsa_keygen_bits:2048
  openssl pkcs8 -topk8 -inform PEM -in "$PRIVATE_KEY_FILE.tmp" -out "$PRIVATE_KEY_FILE" -nocrypt
#  rm "$PRIVATE_KEY_FILE.tmp"
fi

# Check for a public key file
if [ -s "$PUBLIC_KEY_FILE" ]; then
  echo "Public key: $PUBLIC_KEY_FILE exist, not exporting a new one"
else
  openssl rsa -pubout -in "$PRIVATE_KEY_FILE" -out "$PUBLIC_KEY_FILE"
fi
