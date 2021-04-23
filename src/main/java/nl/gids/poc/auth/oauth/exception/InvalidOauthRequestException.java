package nl.gids.poc.auth.oauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 */
@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class InvalidOauthRequestException extends RuntimeException {
	public InvalidOauthRequestException() {
	}

	public InvalidOauthRequestException(String message) {
		super(message);
	}

	public InvalidOauthRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidOauthRequestException(Throwable cause) {
		super(cause);
	}

	public InvalidOauthRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
