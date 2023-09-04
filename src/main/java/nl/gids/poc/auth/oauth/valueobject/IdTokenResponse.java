package nl.gids.poc.auth.oauth.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 *
 */
public class IdTokenResponse extends BearerTokenType {
	@JsonProperty("id_token")
	String idToken;

	public String getIdToken() {
		return idToken;
	}

	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}

}
