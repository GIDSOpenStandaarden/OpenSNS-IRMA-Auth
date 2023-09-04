package nl.gids.poc.auth.oauth.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class OidcTokenResponse extends IdTokenResponse {
	@JsonProperty("access_token")
	String accessToken;
	@JsonProperty("refresh_token")
	String refreshToken;
	@JsonProperty("expires_in")
	Integer expiresIn = 3600;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Integer getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Integer expiresIn) {
		this.expiresIn = expiresIn;
	}
}
