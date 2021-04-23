package nl.gids.poc.auth.oauth.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 *
 */
@Service
public class ClientCredentialValidator {

	static final Log LOG = LogFactory.getLog(ClientCredentialValidator.class);

	public String sign(String userName, PrivateKey privateKey) throws GeneralSecurityException {
		byte[] bytes = userName.getBytes(StandardCharsets.UTF_8);
		Signature sig = Signature.getInstance("SHA1WithRSA");
		sig.initSign(privateKey);
		sig.update(bytes);
		byte[] signatureBytes = sig.sign();
		return Base64.getEncoder().encodeToString(signatureBytes);
	}

	public boolean validate(String clientId, String userName, String password, PublicKey publicKey) throws GeneralSecurityException {
		if (!StringUtils.equals(clientId, userName)) {
			return false;
		}

		Signature publicSignature = Signature.getInstance("SHA1WithRSA");
		publicSignature.initVerify(publicKey);
		publicSignature.update(userName.getBytes(StandardCharsets.UTF_8));

		byte[] signatureBytes = Base64.getDecoder().decode(password);
		try {
			return publicSignature.verify(signatureBytes);
		} catch (SignatureException ex) {
			LOG.warn("Failed to validate signature", ex);
			return false;
		}
	}
}
