package nl.gids.poc.auth.irma.valueobject;

/**
 *
 */
public class JwtResponse {
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	String token;
}
