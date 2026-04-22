package hocjava.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashUtil {
	/*
	 * Mã hóa
	 */
	public static String encode(String value) {
		try {
			var cryp =new BCryptPasswordEncoder();
			return cryp.encode(value);
		}
		catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	/*
	 * Kiểm tra compareValue có match với hashValue không
	 */
	public static boolean isMatch(String rawValue, String encodeValue) {
		try {
			var cryp = new BCryptPasswordEncoder();
			return cryp.matches(rawValue, encodeValue);
		}
		catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
