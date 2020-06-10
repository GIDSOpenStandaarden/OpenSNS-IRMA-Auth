package nl.gids.poc.auth.irma.controller;

import nl.gids.poc.auth.irma.configuration.ApplicationConfiguration;
import nl.gids.poc.auth.irma.services.AuthenticationService;
import nl.gids.poc.auth.irma.services.IrmaService;
import nl.gids.poc.auth.irma.valueobject.JwtResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

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

	@RequestMapping(value = "start", method = RequestMethod.POST, produces = {"application/json"})
	public String startSession(HttpSession httpSession) {
		return irmaService.startSession(getAttribute(httpSession));
	}

	@RequestMapping(value = "end", method = RequestMethod.POST, produces = {"application/json"}, consumes = {"application/json"})
	public AutheticationResponse stopSession(@RequestBody JwtResponse jwtResponse, HttpSession httpSession) throws Exception {

		String userIdentification = irmaService.endSession(jwtResponse.getToken(), getAttribute(httpSession));

		AutheticationResponse response = new AutheticationResponse();
		String redirectUri = (String) httpSession.getAttribute("redirectUri");
		if (StringUtils.isNotEmpty(redirectUri)) {
			response.url = getRedirectUri(userIdentification, redirectUri);
		}
		return response;
	}

	private String getRedirectUri(String userIdentification, String redirectUri) {
		if (StringUtils.contains(redirectUri, "?")) {
			return redirectUri + "&token=" + authenticationService.createJwt(userIdentification, redirectUri);
		} else {
			return redirectUri + "?token=" + authenticationService.createJwt(userIdentification, redirectUri);
		}
	}

	private String getAttribute(HttpSession httpSession) {
		String attribute = (String) httpSession.getAttribute("attribute");
		if (StringUtils.isNotEmpty(attribute)) {
			return attribute;
		}

		return applicationConfiguration.getDefaultAttribute();
	}

	public static class AutheticationResponse {
		public String url = "";
	}

}
