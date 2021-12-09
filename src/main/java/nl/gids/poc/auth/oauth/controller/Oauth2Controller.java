package nl.gids.poc.auth.oauth.controller;

import nl.gids.poc.auth.irma.configuration.ServerConfiguration;
import nl.gids.poc.auth.irma.services.AuthenticationService;
import nl.gids.poc.auth.oauth.exception.InvalidOauthRequestException;
import nl.gids.poc.auth.oauth.service.ClientCredentialValidator;
import nl.gids.poc.auth.oauth.service.OauthSessionService;
import nl.gids.poc.auth.oauth.valueobject.IdTokenResponse;
import nl.gids.poc.auth.oauth.valueobject.OauthSession;
import nl.gids.poc.auth.utils.KeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
@Controller
@RequestMapping("/oauth2")
public class Oauth2Controller {


	final static Log LOG = LogFactory.getLog(Oauth2Controller.class);
	public static final Pattern PATTERN_USERNAME_PASSWORD = Pattern.compile("(.+):(.+)");
	final OauthSessionService oauthSessionService;
	final AuthenticationService authenticationService;
	final ClientCredentialValidator clientCredentialValidator;
	final ServerConfiguration serverConfiguration;

	public Oauth2Controller(OauthSessionService oauthSessionService, AuthenticationService authenticationService, ClientCredentialValidator clientCredentialValidator, ServerConfiguration serverConfiguration) {
		this.oauthSessionService = oauthSessionService;
		this.authenticationService = authenticationService;
		this.clientCredentialValidator = clientCredentialValidator;
		this.serverConfiguration = serverConfiguration;
	}

	@RequestMapping("/authorize")
	public String authorize(@RequestParam("scope") String scope,
							@RequestParam("response_type") String responseType,
							@RequestParam("client_id") String clientId,
							@RequestParam("redirect_uri") String redirectUri,
							@RequestParam("state") String state,
							HttpSession httpSession) {

		if (!StringUtils.equals("code", responseType)) {
			throw new InvalidOauthRequestException("The only supported response_type is code");
		}

		OauthSession oauthSession = new OauthSession();
		oauthSession.setResponseType(responseType);
		oauthSession.setScope(scope);
		oauthSession.setClientId(clientId);
		oauthSession.setRedirectUri(redirectUri);
		oauthSession.setState(state);
		oauthSessionService.store(oauthSession);
		httpSession.setAttribute("oauthSession", oauthSession.getId());
		return "redirect:/irma.html";
	}

	@RequestMapping(value = "/token", produces = "application/json", method = RequestMethod.POST)
	public @ResponseBody
	IdTokenResponse token(
			@RequestParam(value = "code", required = false) String code,
			@RequestParam(value = "redirect_uri", required = false) String redirectUri,
			@RequestParam(value = "refresh_token", required = false) String refreshToken,
			@RequestParam("grant_type") String grantType,
			@RequestHeader("Authorization") String authorization,
			HttpServletRequest request
	) throws IOException, GeneralSecurityException {

		OauthSession oauthSession = oauthSessionService.retrieveByCode(code);
		if (oauthSession == null) {
			throw new InvalidOauthRequestException("Unknown code: " + code);
		}
		if (!validateAuthorization(authorization, oauthSession.getClientId())) {
			throw new InvalidOauthRequestException("Authorization failed.");
		}

		if (StringUtils.equals("id_token", grantType)) {
			if (!StringUtils.equals(oauthSession.getRedirectUri(), redirectUri)) {
				throw new InvalidOauthRequestException("The redirect_uri does not match");
			}
			return tokenIdToken(oauthSession, getIssuer(request));
		} else if (StringUtils.equals("refresh_token", grantType)) {
			return tokenRefresh(refreshToken);
		}
		throw new InvalidOauthRequestException("Unknown grant_type: " + grantType);
	}

	private String getIssuer(HttpServletRequest request) {
		int serverPort = request.getServerPort();
		String scheme = request.getScheme();
		String serverName = request.getServerName();
		if (isDefault(serverPort, scheme)) {
			return String.format("%s://%s", scheme, serverName);
		} else {
			return String.format("%s://%s:%d", scheme, serverName, serverPort);
		}
	}

	private boolean isDefault(int serverPort, String scheme) {
		return (serverPort == 443 && StringUtils.equals(scheme, "https")) || (serverPort == 80 && StringUtils.equals(scheme, "http"));
	}

	private IdTokenResponse tokenIdToken(OauthSession oauthSession, String issuer) {
		IdTokenResponse rv = new IdTokenResponse();
		// Use serverName as issuer.
		rv.setIdToken(authenticationService.createJwt(oauthSession.getUserIdentification(), oauthSession.getClientId(), issuer));
		rv.setTokenType("Bearer");
		return rv;
	}

	private IdTokenResponse tokenRefresh(String refreshToken) {
		throw new InvalidOauthRequestException("Refresh token not implemented on id_token flow.");
	}

	private boolean validateAuthorization(String authorization, String clientId) throws IOException, GeneralSecurityException {
		if (StringUtils.startsWith(authorization, "Basic")) {
			String authorizationLine = StringUtils.trim(StringUtils.removeStart(authorization, "Basic"));
			authorizationLine = new String(Base64.getDecoder().decode(authorizationLine));
			Matcher matcher = PATTERN_USERNAME_PASSWORD.matcher(authorizationLine);
			if (matcher.matches()) {
				String userName = matcher.group(1);
				String password = matcher.group(2);
				return clientCredentialValidator.validate(clientId, userName, password, KeyUtils.getPublicKey(serverConfiguration.getJwtPublicKey()));
			}
		}
		return false;
	}
}
