import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordOperations {

	// Password policy:
	// 1- At least 8 characters. 2- At least one uppercase letter. 3- At least one lowercase letter.
	// 4- At least one digit. 5- At least one special character from: @ _ / . $ ! # &
	public boolean isValidPassword(String password) {
		if (password == null || password.length() < 8) {
			return false;
		}
		return password.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@_/.$!#&]).*");
	}

	// Generate a random 16-byte salt and return it as a hex string
	public String generateSalt() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] salt = new byte[16];
		secureRandom.nextBytes(salt);
		return convertToHex(salt);
	}

	// Hash (salt + password) using SHA-256 and return the result as a hex string
	public String hashPassword(String password, String salt) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String saltedPassword = salt + password;
			byte[] hash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
			return convertToHex(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Convert bytes to a hex string so the hash and salt can be stored easily as text
	private String convertToHex(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}
}
