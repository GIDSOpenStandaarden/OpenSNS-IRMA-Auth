package nl.gids.poc.auth.oauth.service;

import nl.gids.poc.auth.oauth.valueobject.OauthSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Service
public class OauthSessionService {
	private final Map<String, OauthSession> STORE_ID = new HashMap<>();
	private final Map<String, OauthSession> STORE_CODE = new HashMap<>();
	private final Map<String, OauthSession> STORE_REFRESH_TOKEN = new HashMap<>();

	public OauthSession retrieveById(String id) {
		return STORE_ID.get(id);
	}

	public OauthSession retrieveByCode(String code) {
		return STORE_CODE.get(code);
	}

	public OauthSession consumeRefreshToken(String refreshToken) {
		return STORE_REFRESH_TOKEN.remove(refreshToken);
	}

	public void store(OauthSession oauthSession) {
		STORE_ID.put(oauthSession.getId(), oauthSession);
		STORE_CODE.put(oauthSession.getCode(), oauthSession);
		STORE_REFRESH_TOKEN.put(oauthSession.getRefreshToken(), oauthSession);
	}
}
