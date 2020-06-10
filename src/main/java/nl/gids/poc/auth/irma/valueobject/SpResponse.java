package nl.gids.poc.auth.irma.valueobject;

import java.util.HashMap;

/**
 *
 */
public class SpResponse {
	public String token;
	public String status;
	public String type;
	public String proofStatus;
	public Disclosure[][] disclosed;

	public static class Disclosure {
		public String id;
		public String issuancetime;
		public String rawvalue;
		public String status;
		public HashMap<String, String> value;
	}

}
