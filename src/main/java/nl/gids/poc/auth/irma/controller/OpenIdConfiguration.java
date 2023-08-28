package nl.gids.poc.auth.irma.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import nl.gids.poc.auth.utils.UrlUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@RestController
@RequestMapping(".well-known/openid-configuration")
public class OpenIdConfiguration {

    final ObjectMapper objectMapper;

    public OpenIdConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String get(HttpServletRequest request) throws JsonProcessingException {
        Map<String, String> rv = new HashMap<>();
        String baseUrl = UrlUtils.getBaseUrl(request);
        rv.put("issuer", baseUrl);
        rv.put("jwks_uri", String.format("%s/.well-known/jwks.json", baseUrl));
        rv.put("authorization_endpoint", String.format("%s/oauth2/authorize", baseUrl));
        rv.put("token_endpoint", String.format("%s/oauth2/token", baseUrl));
        rv.put("token_endpoint_auth_methods_supported", "client_secret_basic");
        rv.put("scopes_supported", "openid");
        return objectMapper.writeValueAsString(rv);
    }
}
