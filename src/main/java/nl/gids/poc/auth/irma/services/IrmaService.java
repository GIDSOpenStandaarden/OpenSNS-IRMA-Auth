package nl.gids.poc.auth.irma.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import nl.gids.poc.auth.utils.PemUtils;
import nl.gids.poc.auth.irma.configuration.IrmaConfiguration;
import nl.gids.poc.auth.irma.valueobject.SpRequest;
import nl.gids.poc.auth.irma.valueobject.SpResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

/**
 *
 */
@Service
public class IrmaService {

	private static final Log LOG = LogFactory.getLog(IrmaService.class);

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	IrmaConfiguration irmaConfiguration;

	@Autowired
	ValidationService validationService;

	@Autowired
	ObjectMapper objectMapper;

	RSAPublicKey publicKey;

	@PostConstruct
	public void init() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		Assert.isTrue(StringUtils.isNotEmpty(irmaConfiguration.getJwtPublicKey()), "The value for irma.server.jwtPublicKey needs to be configured");
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(getPublicKey()));
		LOG.info(String.format("The JWT validation public key is configured correctly, the public key is:%n%s", PemUtils.formatPublicKey(publicKey)));
	}

	private byte[] getPublicKey() throws IOException {
		return PemUtils.readPemKeyFromFileOrValue(irmaConfiguration.getJwtPublicKey());
	}

	public String endSession(String requestorToken, String attribute) throws JoseException, JsonProcessingException {

		final String resultJwt = getResult(requestorToken);

		final JsonWebStructure jws = JsonWebStructure.fromCompactSerialization(resultJwt);
		jws.setKey(publicKey);
		final String payload = jws.getPayload();

		LOG.info(String.format("Got the following result JWT for requestorToken [%s]:\n\n%s", requestorToken, payload));

		String userIdentification = "";
		SpResponse data = objectMapper.readValue(payload, SpResponse.class);
		for (SpResponse.Disclosure[] disclosures : data.disclosed) {
			for (SpResponse.Disclosure disclosure : disclosures) {
				if (StringUtils.equals(attribute, disclosure.id)) {
					userIdentification = disclosure.rawvalue;
					break;
				}
			}
		}

		LOG.info("Matched userIdentification: " + userIdentification);

		return userIdentification;
	}

	public String startSession(String attribute) {
		validationService.validateAttribute(attribute);
		String issuer = irmaConfiguration.getIssuer();
		String token = irmaConfiguration.getToken();
		String server = irmaConfiguration.getUrl();
		String url = server + "/session";
		SpRequest request = new SpRequest(attribute);
		if (irmaConfiguration.getTimeout() > 0) {
			request.setTimeout(irmaConfiguration.getTimeout());
		}
		String jwtToken = Jwts.builder()
				.setHeaderParam("typ", "jwt")
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 30 * 1000))
				.setIssuer(issuer)
				.setSubject("verification_request")
				.claim("sprequest", request)
				.signWith(SignatureAlgorithm.HS256, token)
				.compact();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
		HttpEntity<String> requestEntity = new HttpEntity<>(jwtToken, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		return response.getBody();
	}

	public String getResult(String requestorToken) {
		String server = irmaConfiguration.getUrl();
		String url = server + "/session/" + requestorToken + "/result-jwt";

		LOG.info("Calling endpoint: " + url);
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		return response.getBody();
	}
}
