package nl.gids.poc.auth.irma.controller;

import nl.gids.poc.auth.irma.configuration.ApplicationConfiguration;
import nl.gids.poc.auth.irma.services.ValidationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.net.URISyntaxException;

/**
 *
 */
@Controller
public class IndexController {
	@Autowired
	ApplicationConfiguration applicationConfiguration;
	@Autowired
	ValidationService validationService;

	@RequestMapping("/")
	public String index(@RequestParam(value = "redirect_uri") String redirectUri, @RequestParam(value = "attribute", required = false) String attribute, HttpSession session) throws URISyntaxException {
		session.setAttribute("redirectUri", redirectUri);
		if (StringUtils.isEmpty(attribute)) {
			attribute = applicationConfiguration.getDefaultAttribute();
		}
		session.setAttribute("attribute", attribute);
		validationService.validateAttribute(attribute);
		validationService.validateRedirectUri(redirectUri);
		return "redirect:irma.html";
	}
}
