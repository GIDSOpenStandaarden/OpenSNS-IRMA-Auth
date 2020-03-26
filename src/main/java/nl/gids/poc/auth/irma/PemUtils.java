package nl.gids.poc.auth.irma;

import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.PublicKey;

/**
 *
 */
public class PemUtils {
	private static final Log LOG = LogFactory.getLog(PemUtils.class);
	public static byte[] readPemKeyString(String pemFormat) {
 		pemFormat = pemFormat.replaceAll("-----(BEGIN|END)[A-Z ]+KEY-----", "");
		pemFormat = pemFormat.replace("\n", "");
		return Base64.decodeBase64(pemFormat);
	}

	public static String formatPublicKey(PublicKey publicKey) {
		byte[] encoded = publicKey.getEncoded();
		String publicKeyContent = java.util.Base64.getEncoder().encodeToString(encoded);
		String publicKeyFormatted = "-----BEGIN PUBLIC KEY-----" + System.lineSeparator();
		for (final String row :
				com.google.common.base.Splitter
						.fixedLength(64)
						.split(publicKeyContent)
		) {
			publicKeyFormatted += row + System.lineSeparator();
		}
		publicKeyFormatted += "-----END PUBLIC KEY-----";
		return publicKeyFormatted;
	}

	public static byte[] readPemKeyFromFileOrValue(String fileName, String stringValue) throws IOException {
		byte[] rv;
		if (StringUtils.isNotEmpty(fileName) && new File(fileName).exists()) {
			LOG.info("Reading file " + fileName+ " as PEM file");
			rv = readPemKeyString(Files.asCharSource(new File(fileName), Charset.defaultCharset()).read());
		} else {
			LOG.info("Reading PEM as configuration value");
			rv = readPemKeyString(stringValue);
		}
		return rv;
	}
}
