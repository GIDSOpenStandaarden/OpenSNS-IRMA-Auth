package nl.gids.poc.auth.irma.controller;

import nl.gids.poc.auth.irma.configuration.ServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 */
@Controller
public class PublicKeyController {
	@Autowired
	private ServerConfiguration serverConfiguration;

	@RequestMapping(value = "/public_key.txt", produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody
	String public_key() {
		return serverConfiguration.getJwtPublicKey();
	}
}
