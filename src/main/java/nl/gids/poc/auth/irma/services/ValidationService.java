package nl.gids.poc.auth.irma.services;

import nl.gids.poc.auth.irma.configuration.ApplicationConfiguration;
import nl.gids.poc.auth.irma.exception.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 *
 */
@Service
public class ValidationService {

	private static final Log LOG = LogFactory.getLog(ValidationService.class);

	@Autowired
	ApplicationConfiguration applicationConfiguration;

	public void validateAttribute(String attribute) {
		if (!isValidAttribute(attribute) && applicationConfiguration.isValidateAttribute()) {
			throw new ValidationException(format("The attribute %s is not allowed. Please make sure the attribute is in the allowed attribute list.", attribute));
		}
	}

	public void validateRedirectUri(String redirectUri) throws URISyntaxException {
		String host = getHostFromUri(redirectUri);
		String domain = getDomainFromHost(host);
		if (!isDomainAllowed(domain) && applicationConfiguration.isValidateRedirectDomain()) {
			throw new ValidationException(format("The domain '%s' is not allowed. Please make sure the domain is in the allowed domain list.", domain));
		}
	}


	private String getDomainFromHost(String host) {
		final Pattern pattern = Pattern.compile("(.*\\.)?([^.]+\\.[^.]+)");
		final Matcher matcher = pattern.matcher(host);
		if (matcher.matches()) {
			return matcher.group(2);
		}
		return host;
	}

	private String getHostFromUri(String redirectUri) throws URISyntaxException {
		URI uri = new URI(redirectUri);
		return uri.getHost();
	}

	private boolean isDomainAllowed(String domain) {
		for (String allowedRedirectDomain : applicationConfiguration.getAllowedRedirectDomains()) {
			if (StringUtils.equals(domain, allowedRedirectDomain)) {
				return true;
			}
		}
		return false;
	}

	private boolean isValidAttribute(String attribute) {
		final List<String> allowedAttributes = applicationConfiguration.getAllowedAttributes();
		for (String allowedAttribute : allowedAttributes) {
			if (matchesAttribute(allowedAttribute, attribute)) {
				return true;
			}
		}
		return false;
	}

	private boolean matchesAttribute(String pattern, String attribute) {
		return Pattern.matches(pattern.replace("*", ".*"), attribute);
	}

}
