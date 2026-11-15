import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseOperations {
	// Private and final: the database location cannot be changed from outside the class
	private static final String DB_URL = "jdbc:sqlite:hashedPasswordsFile.db";

	// Constructor: creates the "users" table if it does not exist
	// (id primary key, username, password_hash, salt).
	// try-with-resources guarantees the connection is closed when we finish.
	public DatabaseOperations() {
		String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "username TEXT NOT NULL,"
				+ "password_hash TEXT NOT NULL,"
				+ "salt TEXT NOT NULL)";
		try (Connection conn = DriverManager.getConnection(DB_URL);
				Statement stmt = conn.createStatement()) {
			stmt.execute(createTableSQL);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// Insert a new user. PreparedStatement (with ? placeholders) protects against SQL injection.
	public void registerUser(String username, String hashedPassword, String salt) {
		String sql = "INSERT INTO users(username, password_hash, salt) VALUES(?, ?, ?)";
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, username);
			pstmt.setString(2, hashedPassword);
			pstmt.setString(3, salt);
			pstmt.execute();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// Login: fetch the stored salt and hash, re-hash the entered password with that salt,
	// then compare the two hashes in constant time.
	public boolean isUserLogin(String username, String password, PasswordOperations po) {
		String sql = "SELECT password_hash, salt FROM users WHERE username = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, username);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					String storedHash = rs.getString("password_hash");
					String storedSalt = rs.getString("salt");
					String hashedPassword = po.hashPassword(password, storedSalt);
					if (hashedPassword == null) {
						return false;
					}
					return MessageDigest.isEqual(
							storedHash.getBytes(StandardCharsets.UTF_8),
							hashedPassword.getBytes(StandardCharsets.UTF_8));
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
}
