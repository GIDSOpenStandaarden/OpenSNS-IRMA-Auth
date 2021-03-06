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

A [developer manual](./src/main/resources/public/manual/manual.pdf) is 
available. This manual describes how to use this component.

# Running

## Running on Docker and Docker compose

### Setting up the .env file for docker and docker compose

Copy the `.env.dist` file to `.env` and add the following values

1. IRMA_SERVER_URL, The URL of the IRMA server. Of you expose the IRMA server with ngrok, the ngrok URL should be set here.

       IRMA_SERVER_URL=http://localhost:8081
       
   or
       
       IRMA_SERVER_URL=https://41bd98bc.ngrok.io

1. IRMA_SERVER_TOKEN, The shared token from the IRMA server

       IRMA_SERVER_TOKEN=f1aa25cd6fc...

1. GIDS_SERVER_JWTPUBLICKEY/GIDS_SERVER_JWTPRIVATEKEY. Generate a keypair in the root directory by running:
        
        cd keys
        ../tools/keygen.sh
        cd -
        
   and make sure to add the following environment variables:
   
        GIDS_SERVER_JWTPUBLICKEY=/keys/gids_public_key.pem
        GIDS_SERVER_JWTPRIVATEKEY=/keys/gids_private_key.pem
        

## Docker
To start the server with the default configuration, run. 
```shell script
docker build . -t irma_server
docker run -p 8082:8080 --env-file=.env --name irma_server irma_server
```

# Docker compose
```shell script
cp .env.dist .env
docker-compose build && docker-compose up
```

Now you can access the application on http://gids-irma-auth.localhost:8082/?redirect_uri=http://www.nu.nl.

# Configuration

## Environment variables


| Variable | default | remark |
| ---: | --- | :--- |
| IRMA_SERVER_URL               |        | The URL of the IRMA server. |
| IRMA_SERVER_ISSUER            | testsp | The API client access token of the IRMA server.  |
| IRMA_SERVER_TOKEN             |        | The API client access token of the IRMA server.  |
| GIDS_SERVER_JWTPUBLICKEY      | \[generated if absent]* | The public key of the GIDS server, used for JWT signing. The value can be a PEM encoded string or file. |
| GIDS_SERVER_JWTPRIVATEKEY     | \[generated if absent]* | The private key of the GIDS server, used for JWT signing. The value can be a PEM encoded string or file. |
 
\* If both values for GIDS_SERVER_JWTPUBLICKEY and GIDS_SERVER_JWTPRIVATEKEY are absent, a keypair will be generated as startup.
