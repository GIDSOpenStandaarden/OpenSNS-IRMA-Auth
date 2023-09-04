package nl.gids.poc.auth.oauth.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BearerTokenType {

    @JsonProperty("token_type")
    String tokenType = "Bearer";

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
