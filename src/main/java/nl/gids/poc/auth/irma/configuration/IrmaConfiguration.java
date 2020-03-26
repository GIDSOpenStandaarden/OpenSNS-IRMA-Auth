package nl.gids.poc.auth.irma.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
@ConfigurationProperties("irma.server")
public class IrmaConfiguration {
	private String issuer;
	private String token;
	private String url;
	private String jwtPublicKey;
	private String jwtPublicKeyFile;

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
