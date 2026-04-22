package hocjava.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService, PasswordEncoder encoder) {
	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
	    authProvider.setPasswordEncoder(encoder);
	    authProvider.setHideUserNotFoundExceptions(false); 
	    
	    return authProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
					.requestMatchers("/error").permitAll()
					.requestMatchers("/admin/**")
					.authenticated().anyRequest().permitAll()
				)
				.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/admin/dashboard").failureUrl("/login?error=true"))
				.logout(logout -> logout
						.logoutRequestMatcher(PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/logout"))
			            .logoutSuccessUrl("/login?logout")
			            .permitAll()
			        )
				.rememberMe(Customizer.withDefaults());
		return http.build();
	}

}
