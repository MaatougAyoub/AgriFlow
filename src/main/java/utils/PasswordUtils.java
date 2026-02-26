package utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Hash a plaintext password using BCrypt
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) return null;
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    // Verify a plaintext password against a BCrypt hash
    public static boolean verifyPassword(String plainPassword, String hashed) {
        if (plainPassword == null || hashed == null) return false;
        return BCrypt.checkpw(plainPassword, hashed);
    }
}
