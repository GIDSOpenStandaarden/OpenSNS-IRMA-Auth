# IRMA authentication service

The goal of this component is to abstract the IRMA “implementation complexity” away from other applications in the PoC.
I quote “implementation complexity”  because in my experience integrating IRMA is not very complex, however, one need 
to understand some core concepts that demand some attention.
 
## Architecture and workflow

1. The architecture is described in the figure below and consists of the following workflow:
1. Your application redirects the client browser to the GIDS IRMA PoC implementation with the redirect_uri parameter in an URL.
1. The browser opens the URL
1. The GIDS IRMA PoC does the IRMA magic
1. The GIDS IRMA PoC redirects the client browser to the redirect_uri with a JWT token in the parameters
1. The browser opens the redirect_uri with the JWT token
1. Your application decodes the JWT token and authenticates the user with the subject (sub) in the JWT token.

## The manual

A [developer manual](https://docs.google.com/document/d/1oYt41LxmhstPicUIj8QRqZG2zD7oDV2DvQq_omOYroY/edit?usp=sharing) is 
available. This manual describes how to use this component.

# Running

## Running on Docker and Docker compose

### Setting up the .env file for docker and docker compose
Edit the .env file and add the following values

1. IRMA_SERVER_URL, The URL of the IRMA server

       IRMA_SERVER_URL=http://localhost:8081

1. IRMA_SERVER_TOKEN, The shared token from the IRMA server

       IRMA_SERVER_TOKEN=f1aa25cd6fc...

1. IRMA_SERVER_JWTPUBLICKEYFILE, the JWT public key from your irma server. Make sure the file ends with
.pem and is located in the /keys folder. The format must be pem (-----BEGIN PUBLIC KEY----- ... -----END PUBLIC KEY-----)

       IRMA_SERVER_JWTPUBLICKEYFILE=/keys/mypublickeyfrommyirmaserver.pem

1. GIDS_SERVER_JWTPUBLICKEYFILE/GIDS_SERVER_JWTPRIVATEKEYFILE. Generate a keypair in the root directory by running:
        
        cd keys
        ../tools/keygen.sh
        cd -
        
   and make sure to add the following environment variables:
   
        GIDS_SERVER_JWTPUBLICKEYFILE=/keys/gids_public_key.pem
        GIDS_SERVER_JWTPRIVATEKEYFILE=/keys/gids_private_key.pem
        

## Docker
To start the server with the default configuration, run. 
```shell script
mvn --quiet clean install && docker build . -t irma_server
docker run -p 8080:8080 --name irma_server irma_server
```

# Docker compose
```shell script
mvn --quiet clean install && docker-compose build && docker-compose up
```

## Running on kubernetes
Please see the  [kubernetes](./kubernetes) directory for the example scripts.

# Configuration

## Environment variables


| Variable | default | remark |
| ---: | --- | :--- |
| IRMA_SERVER_URL               |        | The URL of the IRMA server. |
| IRMA_SERVER_JWTPUBLICKEYFILE  |        | The public key of the IRMA server. |
| IRMA_SERVER_ISSUER            | testsp | The API client access token of the IRMA server.  |
| IRMA_SERVER_TOKEN             |        | The API client access token of the IRMA server.  |
| GIDS_SERVER_JWTPUBLICKEYFILE  |  \[generated if absent] | Optional method of referring to a public key file added to the container. |
| GIDS_SERVER_JWTPRIVATEKEYFILE  | \[generated if absent]  | If GIDS_SERVER_JWTPRIVATEKEY and GIDS_SERVER_JWTPRIVATEKEYFILE are not set or not persent, the container will generate a keypair on startup. |
 
