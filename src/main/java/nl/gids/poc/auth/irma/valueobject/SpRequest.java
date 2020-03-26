package nl.gids.poc.auth.irma.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.gids.poc.auth.irma.controller.SessionController;

/**
 *
 */
public class SpRequest {
	public Request request;

	public SpRequest(String attribute) {
		request = new Request(attribute);
	}

	public static class Request {
		@JsonProperty("@context")
		public String context = "https://irma.app/ld/request/disclosure/v2";
		public String[][][] disclose;

		public Request(String attribute) {
			/**
			 * {{"pbdf.gemeente.personalData.bsn"}}, {{"pbdf.gemeente.personalData.over16"}}, {{"pbdf.pbdf.email.email"}}
			 */
			disclose = new String[][][]{{{attribute}}};
		}
	}
}
