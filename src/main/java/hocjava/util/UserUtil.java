package hocjava.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {
	public static Authentication getCurrentAuthen() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
}
