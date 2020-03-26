package nl.gids.poc.auth.irma.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
@ConfigurationProperties("gids.server")
public class ServerConfiguration {
	private String jwtPublicKey;
	private String jwtPrivateKey;
	private String issuer;

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getJwtPrivateKey() {
		return jwtPrivateKey;
	}

	public void setJwtPrivateKey(String jwtPrivateKey) {
		this.jwtPrivateKey = jwtPrivateKey;
	}

	public String getJwtPublicKey() {
		return jwtPublicKey;
	}

	public void setJwtPublicKey(String jwtPublicKey) {
		this.jwtPublicKey = jwtPublicKey;
	}

}
