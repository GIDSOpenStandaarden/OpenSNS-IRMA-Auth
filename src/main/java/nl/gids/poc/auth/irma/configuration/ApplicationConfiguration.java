package nl.gids.poc.auth.irma.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 *
 */
@Configuration
@ConfigurationProperties("app.irma")
public class ApplicationConfiguration {
	private String defaultAttribute;

	private List<String> allowedAttributes;

	private List<String> allowedRedirectDomains;

	private boolean validateRedirectDomain;
	private boolean validateAttribute;

	public List<String> getAllowedAttributes() {
		return allowedAttributes != null ? allowedAttributes : Collections.emptyList();
	}

	public void setAllowedAttributes(List<String> allowedAttributes) {
		this.allowedAttributes = allowedAttributes;
	}

	public List<String> getAllowedRedirectDomains() {
		return allowedRedirectDomains != null ? allowedRedirectDomains : Collections.emptyList();
	}

	public void setAllowedRedirectDomains(List<String> allowedRedirectDomains) {
		this.allowedRedirectDomains = allowedRedirectDomains;
	}

	public String getDefaultAttribute() {
		return defaultAttribute;
	}

	public void setDefaultAttribute(String defaultAttribute) {
		this.defaultAttribute = defaultAttribute;
	}

	public boolean isValidateAttribute() {
		return validateAttribute;
	}

	public void setValidateAttribute(boolean validateAttribute) {
		this.validateAttribute = validateAttribute;
	}

	public boolean isValidateRedirectDomain() {
		return validateRedirectDomain;
	}

	public void setValidateRedirectDomain(boolean validateRedirectDomain) {
		this.validateRedirectDomain = validateRedirectDomain;
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
}
