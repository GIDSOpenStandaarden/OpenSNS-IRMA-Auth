/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gids.poc.auth.irma.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.lang.JoseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
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
				new X509EncodedKeySpec(getEncodedKey(publicKey)));
	}

	private static byte[] getEncodedKey(String key) {
		try {
			BufferedReader br = new BufferedReader(new StringReader(key));
			StringBuilder rawKey = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				if (!StringUtils.startsWith(line, "----")) {
					rawKey.append(line);
				}
			}

			return Base64.decodeBase64(rawKey.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getFingerPrint(PublicKey publicKey) {
		try {
			final JsonWebKey jsonWebKey = JsonWebKey.Factory.newJwk(publicKey);
			return jsonWebKey.calculateBase64urlEncodedThumbprint("MD5");
		} catch (JoseException e) {
			throw new RuntimeException(e);
		}
	}

}
