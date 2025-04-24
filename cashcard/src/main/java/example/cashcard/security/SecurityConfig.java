package example.cashcard.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	// When we add spring security dependency, we need to provide few beans to
	// application context for
	// auto-configuration. One such bean is SecurityFileterChain.
	@Bean
	SecurityFilterChain f(HttpSecurity http) throws Exception {

		// http --> authorizeHttpRequests(lambda) // URL pattern and whether to keep eye
		// on or not?
		// --> csrf(lambda) // to enable csrf or not.
		// --> httpBasic(someValue) // how to authenticate... withDefaults seems like
		// Basic-Auth i.e. username-password.

		http.authorizeHttpRequests(request -> request.requestMatchers("/cashcards/**").hasRole("CARD-OWNER"))
				.httpBasic(Customizer.withDefaults()).csrf(csrf -> csrf.disable());
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Temporary user details.
	// Later on, we can come up with robust user management system separately.
	// Roles-Access table
	// User-Role table
	// UserDetail table
	@Bean
	UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
		User.UserBuilder users = User.builder();
		UserDetails sarah = users.username("sarah1")
				.password(passwordEncoder.encode("abc123"))
				.roles("CARD-OWNER")
				.build();

		UserDetails hankOwnsNoCards = users.username("hank-owns-no-cards").password(passwordEncoder.encode("qrs456"))
				.roles("NON-OWNER") // new role
				.build();
		return new InMemoryUserDetailsManager(sarah, hankOwnsNoCards);
	}

}
