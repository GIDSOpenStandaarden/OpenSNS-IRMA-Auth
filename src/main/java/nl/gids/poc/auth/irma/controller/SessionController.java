package nl.gids.poc.auth.irma.controller;

import jakarta.servlet.http.HttpSession;
import nl.gids.poc.auth.irma.configuration.ApplicationConfiguration;
import nl.gids.poc.auth.irma.services.AuthenticationService;
import nl.gids.poc.auth.irma.services.IrmaService;
import nl.gids.poc.auth.oauth.service.OauthSessionService;
import nl.gids.poc.auth.oauth.valueobject.OauthSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 */
@RestController
@RequestMapping("/api/session")
public class SessionController {

	@Autowired
	IrmaService irmaService;

	@Autowired
	AuthenticationService authenticationService;

	@Autowired
	ApplicationConfiguration applicationConfiguration;

	@Autowired
	OauthSessionService oauthSessionService;

	@RequestMapping(value = "start", method = RequestMethod.POST, produces = {"application/json"})
	public String startSession(HttpSession httpSession) {
		return irmaService.startSession(getAttribute(httpSession));
	}

	@RequestMapping(value = "end/{sessionToken}", method = RequestMethod.POST, produces = {"application/json"})
	public AutheticationResponse stopSession(@PathVariable String sessionToken, HttpSession httpSession) throws Exception {

		String userIdentification = irmaService.endSession(sessionToken, getAttribute(httpSession));

		AutheticationResponse response = new AutheticationResponse();
		String oauthSessionId = (String) httpSession.getAttribute("oauthSession");
		String redirectUri = (String) httpSession.getAttribute("redirectUri");
		if (StringUtils.isNotEmpty(oauthSessionId)) {
			OauthSession oauthSession = oauthSessionService.retrieveById(oauthSessionId);
			oauthSession.setUserIdentification(userIdentification);
			String url = oauthSession.getRedirectUri();
			url = appendToUrl(url, "code", oauthSession.getCode());
			url = appendToUrl(url, "state", oauthSession.getState());
			response.url = url;
		} else if (StringUtils.isNotEmpty(redirectUri)) {
			response.url = getRedirectUri(userIdentification, redirectUri);
		}
		return response;
	}

	private String getAttribute(HttpSession httpSession) {
		String attribute = (String) httpSession.getAttribute("attribute");
		if (StringUtils.isNotEmpty(attribute)) {
			return attribute;
		}

		return applicationConfiguration.getDefaultAttribute();
	}

	private String getRedirectUri(String userIdentification, String redirectUri) {
		String token = authenticationService.createJwt(userIdentification, redirectUri);
		return appendToUrl(redirectUri, "token", token);
	}

	private String appendToUrl(String url, String key, String value) {
		if (StringUtils.contains(url, "?")) {
			return url + "&" + key + "=" + value;
		} else {
			return url + "?" + key + "=" + value;
		}
	}

	public static class AutheticationResponse {
		public String url = "";
	}

}
