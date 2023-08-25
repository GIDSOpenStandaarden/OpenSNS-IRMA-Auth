package nl.gids.poc.auth.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


/**
 *
 */
public class UrlUtils {
    public static String getBaseUrl(HttpServletRequest request) {
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents components = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        String scheme = components.getScheme();
        String host = components.getHost();
        int port = components.getPort();
        if (isDefault(port, scheme)) {
            return String.format("%s://%s", scheme, host);
        } else {
            return String.format("%s://%s:%d", scheme, host, port);
        }
    }

    public static boolean isDefault(int serverPort, String scheme) {
        return serverPort == -1 || (serverPort == 443 && StringUtils.equals(scheme, "https")) || (serverPort == 80 && StringUtils.equals(scheme, "http"));
    }
}
