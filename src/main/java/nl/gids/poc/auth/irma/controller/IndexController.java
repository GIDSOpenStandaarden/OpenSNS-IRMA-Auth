package nl.gids.poc.auth.irma.controller;

import jakarta.servlet.http.HttpSession;
import nl.gids.poc.auth.irma.configuration.ApplicationConfiguration;
import nl.gids.poc.auth.irma.services.ValidationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URISyntaxException;

/**
 *
 */
@Controller
public class IndexController {
	private final ApplicationConfiguration applicationConfiguration;
	private final ValidationService validationService;

	@Autowired
	public IndexController(ApplicationConfiguration applicationConfiguration, ValidationService validationService) {
		this.applicationConfiguration = applicationConfiguration;
		this.validationService = validationService;
	}

	@RequestMapping("/")
	public String index(
			@RequestParam(value = "redirect_uri") String redirectUri,
			@RequestParam(value = "attribute", required = false) String attribute,
			Model model,
			HttpSession session) throws URISyntaxException {

		session.setAttribute("redirectUri", redirectUri);
		if (StringUtils.isEmpty(attribute)) {
			attribute = applicationConfiguration.getDefaultAttribute();
		}
		session.setAttribute("attribute", attribute);
		validationService.validateAttribute(attribute);
		validationService.validateRedirectUri(redirectUri);

		//TODO: This is temporary code, can be removed after SIDN adds this login into the Yivi app. Can be configured
		// if this is a more regular use-case
		if("pbdf.sidn-pbdf.uniqueid.uniqueid".equals(attribute)) {
			model.addAttribute("registerUrl", "https://middleware.vkn.gidsopenstandaarden.org/irma_put.html");
		}

		return "yivi";
	}
}
