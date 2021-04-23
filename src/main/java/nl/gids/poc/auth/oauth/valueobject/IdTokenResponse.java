package nl.gids.poc.auth.oauth.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class IdTokenResponse {
	@JsonProperty("id_token")
	String idToken;
	@JsonProperty("token_type")
	String tokenType = "Bearer";

	public String getIdToken() {
		return idToken;
	}

	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
}
