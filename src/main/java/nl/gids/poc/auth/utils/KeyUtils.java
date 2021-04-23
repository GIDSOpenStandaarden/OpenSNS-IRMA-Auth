/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gids.poc.auth.utils;

import org.jose4j.jwk.JsonWebKey;
import org.jose4j.lang.JoseException;

import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 *
 */
public class KeyUtils {

	/**
	 * Parses a public key to an instance of {@link RSAPublicKey}.
	 *
	 * @param publicKey the string representation of the public key.
	 * @return an instance of {@link RSAPublicKey}.
	 */
	public static RSAPublicKey getRsaPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return (RSAPublicKey) keyFactory.generatePublic(
				new X509EncodedKeySpec(PemUtils.readPemKeyString(publicKey)));
	}

	public static String getFingerPrint(PublicKey publicKey) {
		try {
			final JsonWebKey jsonWebKey = JsonWebKey.Factory.newJwk(publicKey);
			return jsonWebKey.calculateBase64urlEncodedThumbprint("MD5");
		} catch (JoseException e) {
			throw new RuntimeException(e);
		}
	}

	public static RSAPrivateKey getPrivateKey(String key) throws IOException, GeneralSecurityException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(PemUtils.readPemKeyFromFileOrValue(key)));
	}

	public static RSAPublicKey getPublicKey(String key) throws IOException, GeneralSecurityException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(PemUtils.readPemKeyFromFileOrValue(key)));
	}
}
