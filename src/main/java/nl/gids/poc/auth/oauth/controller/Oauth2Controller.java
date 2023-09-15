package nl.gids.poc.auth.oauth.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nl.gids.poc.auth.irma.configuration.ApplicationConfiguration;
import nl.gids.poc.auth.irma.configuration.ServerConfiguration;
import nl.gids.poc.auth.irma.exception.ValidationException;
import nl.gids.poc.auth.irma.services.AuthenticationService;
import nl.gids.poc.auth.oauth.exception.InvalidOauthRequestException;
import nl.gids.poc.auth.oauth.service.ClientCredentialValidator;
import nl.gids.poc.auth.oauth.service.OauthSessionService;
import nl.gids.poc.auth.oauth.valueobject.BearerTokenType;
import nl.gids.poc.auth.oauth.valueobject.IdTokenResponse;
import nl.gids.poc.auth.oauth.valueobject.OidcTokenResponse;
import nl.gids.poc.auth.oauth.valueobject.OauthSession;
import nl.gids.poc.auth.utils.KeyUtils;
import nl.gids.poc.auth.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
	final ApplicationConfiguration applicationConfiguration;

	public Oauth2Controller(OauthSessionService oauthSessionService, AuthenticationService authenticationService, ClientCredentialValidator clientCredentialValidator, ServerConfiguration serverConfiguration, ApplicationConfiguration applicationConfiguration) {
		this.oauthSessionService = oauthSessionService;
		this.authenticationService = authenticationService;
		this.clientCredentialValidator = clientCredentialValidator;
		this.serverConfiguration = serverConfiguration;
		this.applicationConfiguration = applicationConfiguration;
	}

	@RequestMapping("/authorize")
	public String authorize(@RequestParam("scope") String scope,
							@RequestParam("response_type") String responseType,
							@RequestParam("client_id") String clientId,
							@RequestParam("redirect_uri") String redirectUri,
							@RequestParam("state") String state,
							@RequestParam(value = "nonce", required = false) String nonce,
							@RequestParam(value = "yivi_attribute", required = false) String yiviAttribute,
							Model model,
							HttpSession httpSession) {

		if (!StringUtils.equals("code", responseType)) {
			throw new InvalidOauthRequestException("The only supported response_type is code");
		}

		OauthSession oauthSession = new OauthSession();
		oauthSession.setResponseType(responseType);
		oauthSession.setScope(scope);
		oauthSession.setNonce(nonce);
		oauthSession.setClientId(clientId);
		oauthSession.setRedirectUri(redirectUri);
		oauthSession.setState(state);
		oauthSessionService.store(oauthSession);
		httpSession.setAttribute("oauthSession", oauthSession.getId());

		httpSession.setAttribute("redirectUri", redirectUri);

		if (StringUtils.isEmpty(yiviAttribute)) {
			yiviAttribute = applicationConfiguration.getDefaultAttribute();
		}
		//TODO: This is temporary code, can be removed after SIDN adds this login into the Yivi app. Can be configured
		// if this is a more regular use-case
		else if("pbdf.sidn-pbdf.uniqueid.uniqueid".equals(yiviAttribute)) {
			model.addAttribute("registerUrl", "https://middleware.vkn.gidsopenstandaarden.org/irma_put.html");
		}

		httpSession.setAttribute("attribute", yiviAttribute);

		return "yivi";
	}

	@RequestMapping(value = "/token", produces = "application/json", method = RequestMethod.POST)
	public @ResponseBody
	BearerTokenType token(
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

		if (StringUtils.equals("id_token", grantType) || StringUtils.equals("authorization_code", grantType)) {
			if (!StringUtils.equals(oauthSession.getRedirectUri(), redirectUri)) {
				throw new InvalidOauthRequestException("The redirect_uri does not match");
			}
			if("authorization_code".equals(grantType)) {
				return getOidcToken(oauthSession, UrlUtils.getBaseUrl(request));
			} else {
				return getIdToken(oauthSession);
			}
		} else if (StringUtils.equals("refresh_token", grantType)) {
			return tokenRefresh(refreshToken);
		}
		throw new InvalidOauthRequestException("Unknown grant_type: " + grantType);
	}

	private IdTokenResponse getIdToken(OauthSession oauthSession) {
		IdTokenResponse rv = new IdTokenResponse();

		rv.setIdToken(authenticationService.createIdToken(oauthSession));

		return rv;
	}

	private OidcTokenResponse getOidcToken(OauthSession oauthSession, String audience) {
		OidcTokenResponse rv = new OidcTokenResponse();

		rv.setIdToken(authenticationService.createIdToken(oauthSession));
		rv.setAccessToken(authenticationService.createAccessToken(oauthSession, audience));

		String refreshToken = authenticationService.createRefreshToken(oauthSession, audience);
		rv.setRefreshToken(refreshToken);

		oauthSession.setRefreshToken(refreshToken);
		oauthSessionService.store(oauthSession);

		return rv;
	}

	private OidcTokenResponse tokenRefresh(String refreshToken) {

		LOG.info("Received refresh token request");

		if(StringUtils.isBlank(refreshToken)) {
			throw new ValidationException("Invalid refresh_token");
		}

		OauthSession oauthSession = oauthSessionService.consumeRefreshToken(refreshToken);
		if(oauthSession == null) {
			LOG.info("Refresh token not found");
			throw new ValidationException(); // Not providing any information to the client
		}

		LOG.info("Found refresh token");

		try {
			Claims claims = Jwts.parser()
					.setSigningKey(serverConfiguration.getJwtPrivateKey())
					.parseClaimsJws(refreshToken)
					.getBody();

			LOG.info("Refresh token is valid, renewing access_token, id_token and refresh_token");

			return getOidcToken(oauthSession, claims.getAudience());
		} catch(ExpiredJwtException e) {
			// refresh token expired
			throw new ValidationException(); // Not providing any information to the client
		}
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
