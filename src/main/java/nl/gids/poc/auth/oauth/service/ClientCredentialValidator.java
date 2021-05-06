package nl.gids.poc.auth.oauth.service;

import nl.gids.poc.auth.oauth.configuration.Oauth2Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.Base64;

/**
 *
 */
@Service
public class ClientCredentialValidator {

	static final Log LOG = LogFactory.getLog(ClientCredentialValidator.class);

	private final Oauth2Configuration oauth2Configuration;

	public ClientCredentialValidator(Oauth2Configuration oauth2Configuration) {
		this.oauth2Configuration = oauth2Configuration;
	}

	public boolean validate(String clientId, String userName, String password, PublicKey publicKey) throws GeneralSecurityException {
		if (!StringUtils.equals(clientId, userName)) {
			return false;
		}

		String toHash = oauth2Configuration.getSecret() + clientId + oauth2Configuration.getSecret() + clientId;

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(toHash.getBytes());
		String expectedPassword = Base64.getEncoder().encodeToString(md.digest());
		boolean rv = StringUtils.equals(expectedPassword, password);

		if (!rv) {
			LOG.info(String.format("Failed to validate password %s, expecting %s", password, expectedPassword));
		}
		return rv;
	}
}
