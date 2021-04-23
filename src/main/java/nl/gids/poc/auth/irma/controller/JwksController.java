/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gids.poc.auth.irma.controller;

import nl.gids.poc.auth.irma.configuration.ServerConfiguration;
import nl.gids.poc.auth.utils.KeyUtils;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

/**
 *
 */
@RestController
@RequestMapping(".well-known/jwks.json")
public class JwksController {
	@Autowired
	private final ServerConfiguration serverConfiguration;

	public JwksController(ServerConfiguration serverConfiguration) {
		this.serverConfiguration = serverConfiguration;
	}

	@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public String get() throws InvalidKeySpecException, NoSuchAlgorithmException, JoseException {
		RSAPublicKey rsaPublicKey = KeyUtils.getRsaPublicKey(serverConfiguration.getJwtPublicKey());
		JsonWebKey jsonWebKey = JsonWebKey.Factory.newJwk(rsaPublicKey);
		jsonWebKey.setKeyId(KeyUtils.getFingerPrint(rsaPublicKey));
		JsonWebKeySet jsonWebKeySet = new JsonWebKeySet(jsonWebKey);
		return jsonWebKeySet.toJson();
	}

}
