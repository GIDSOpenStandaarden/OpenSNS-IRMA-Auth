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
	private String jwtPublicKeyFile;
	private String jwtPrivateKeyFile;
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

	public String getJwtPrivateKeyFile() {
		return jwtPrivateKeyFile;
	}

	public void setJwtPrivateKeyFile(String jwtPrivateKeyFile) {
		this.jwtPrivateKeyFile = jwtPrivateKeyFile;
	}

	public String getJwtPublicKey() {
		return jwtPublicKey;
	}

	public void setJwtPublicKey(String jwtPublicKey) {
		this.jwtPublicKey = jwtPublicKey;
	}

	public String getJwtPublicKeyFile() {
		return jwtPublicKeyFile;
	}

	public void setJwtPublicKeyFile(String jwtPublicKeyFile) {
		this.jwtPublicKeyFile = jwtPublicKeyFile;
	}
}
