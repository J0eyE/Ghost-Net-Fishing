package org.sheashepherd.ghostnet.security;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {
	private static final SecureRandom RNG = new SecureRandom();
	private static final int ITERATIONS = 120_000;
	private static final int KEYLEN = 256; // bits
	private static final String ALGO = "PBKDF2WithHmacSHA256";

	private PasswordUtil() {
	}

	public static String newSaltBase64() {
		byte[] salt = new byte[16];
		RNG.nextBytes(salt);
		return Base64.getEncoder().encodeToString(salt);
	}

	public static String hash(char[] password, String saltB64) {
		try {
			byte[] salt = Base64.getDecoder().decode(saltB64);
			PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEYLEN);
			SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGO);
			byte[] key = skf.generateSecret(spec).getEncoded();
			return Base64.getEncoder().encodeToString(key);
		} catch (Exception e) {
			throw new IllegalStateException("Password hashing failed", e);
		}
	}

	public static boolean verify(char[] password, String saltB64, String expectedHashB64) {
		String h = hash(password, saltB64);
		return constantTimeEquals(h, expectedHashB64);
	}

	private static boolean constantTimeEquals(String a, String b) {
		if (a == null || b == null)
			return false;
		if (a.length() != b.length())
			return false;
		int res = 0;
		for (int i = 0; i < a.length(); i++)
			res |= a.charAt(i) ^ b.charAt(i);
		return res == 0;
	}
}
