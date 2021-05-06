package nl.gids.poc.auth.oauth.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 */

@Configuration
@ConfigurationProperties("oauth2")
public class Oauth2Configuration {
	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	String secret;
}
