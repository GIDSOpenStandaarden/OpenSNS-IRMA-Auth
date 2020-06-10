package nl.gids.poc.auth.irma.valueobject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class SpRequest {
	private Request request;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer timeout = null;

	public SpRequest(String attribute) {

		request = new Request(attribute);
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
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
