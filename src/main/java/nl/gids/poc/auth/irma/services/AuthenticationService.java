package nl.gids.poc.auth.irma.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import nl.gids.poc.auth.irma.configuration.ServerConfiguration;
import nl.gids.poc.auth.oauth.valueobject.OauthSession;
import nl.gids.poc.auth.utils.KeyUtils;
import nl.gids.poc.auth.utils.PemUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 */
@Service
public class AuthenticationService {

	private static final Log LOG = LogFactory.getLog(AuthenticationService.class);
	public static final int TOKEN_EXPIRY = 60 * 60 * 1000; // 1 Hour
	protected RSAPublicKey publicKey;
	protected RSAPrivateKey privateKey;
	@Autowired
	ServerConfiguration serverConfiguration;

	public String createJwt(String userId, String audience)  {
		return createJwt(userId, audience, serverConfiguration.getIssuer());

	}

	public String createJwt(String userId, String audience, String issuer) {
		return Jwts.builder().signWith(SignatureAlgorithm.RS256, privateKey)
				.setHeaderParam("kid", KeyUtils.getFingerPrint(publicKey))
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRY))
				.setIssuer(issuer)
				.setAudience(audience)
				.setSubject(userId)
				.compact();
	}

	@PostConstruct
	public void init() throws Exception {
		Assert.isTrue(StringUtils.isNotEmpty(serverConfiguration.getJwtPublicKey()), "The value for gids.server.jwtPublicKey needs to be configured.");
		Assert.isTrue(StringUtils.isNotEmpty(serverConfiguration.getJwtPrivateKey()), "The value for gids.server.jwtPrivateKey needs to be configured.");
		publicKey = KeyUtils.getPublicKey(serverConfiguration.getJwtPublicKey());
		privateKey = KeyUtils.getPrivateKey(serverConfiguration.getJwtPrivateKey());
		validate(publicKey, privateKey);
	}

	private void validate(RSAPublicKey publicKey, RSAPrivateKey privateKey) throws GeneralSecurityException {
		// create a challenge
		byte[] challenge = new byte[10000];
		ThreadLocalRandom.current().nextBytes(challenge);

		// sign using the private key
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(privateKey);
		signature.update(challenge);
		byte[] signed = signature.sign();

		// verify signature using the public key
		signature.initVerify(publicKey);
		signature.update(challenge);

		Assert.isTrue(signature.verify(signed), "The public and private key do NOT match");
		LOG.info(String.format("The JWT signing keypair is configured correctly, the public key is:%n%s", PemUtils.formatPublicKey(publicKey)));
	}

	public String createAccessToken(OauthSession oauthSession, String issuer) {
		return Jwts.builder().signWith(SignatureAlgorithm.RS256, privateKey)
				.setHeaderParam("kid", KeyUtils.getFingerPrint(publicKey))
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRY))
				.setIssuer(issuer)
				.setAudience(oauthSession.getClientId())
				.setSubject(oauthSession.getUserIdentification())
				.addClaims(Map.of(
						"scope", oauthSession.getScope()
				))
				.compact();
	}
}
