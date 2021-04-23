package nl.gids.poc.auth.oauth.valueobject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 */
public class OauthSession implements Serializable {
	final private String id = UUID.randomUUID().toString();
	final private String code = UUID.randomUUID().toString();
	private String scope;
	@JsonProperty("response_type")
	private String responseType;
	@JsonProperty("client_id")
	private String clientId;
	@JsonProperty("redirect_uri")
	private String redirectUri;
	private String state;
	@JsonIgnore
	private String userIdentification;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getCode() {
		return code;
	}

	public String getId() {
		return id;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUserIdentification() {
		return userIdentification;
	}

	public void setUserIdentification(String userIdentification) {
		this.userIdentification = userIdentification;
	}
}
