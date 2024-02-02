package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(c -> c.ignoringRequestMatchers("auth/login", "auth/logout")
						.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
				.cors(c -> c.disable())
				.authorizeHttpRequests((requests) -> requests
						.requestMatchers("/", "/css/**", "js/**", "/image/**").permitAll()
						.requestMatchers("/*.ico").permitAll()
						.anyRequest().authenticated())
				.formLogin((form) -> form
						.loginPage("/auth/login")
						.permitAll())
				.logout((logout) -> {
					logout
							.logoutSuccessUrl("/auth/login?logout");
					String logoutUrl = "/auth/logout";
					logout.logoutRequestMatcher(
							new OrRequestMatcher(
									new AntPathRequestMatcher(logoutUrl, "GET"),
									new AntPathRequestMatcher(logoutUrl, "POST"),
									new AntPathRequestMatcher(logoutUrl, "PUT"),
									new AntPathRequestMatcher(logoutUrl, "DELETE")))
							.invalidateHttpSession(false)
							.clearAuthentication(true);
					logout.permitAll();
				});

		return http.build();
	}

	@Bean
	UserDetailsService userDetailsService() {
		UserDetails user = User.builder()
				.username("user")
				.password("{bcrypt}$2a$10$RmJFT0Z6l3x4ktJ.O.t80uL6gy/LSPZ8TIfpC1l1bQ/X.g5eEibaa")
				.roles("USER")
				.build();

		UserDetails admin = User.builder()
				.username("admin")
				.password("{bcrypt}$2a$10$RmJFT0Z6l3x4ktJ.O.t80uL6gy/LSPZ8TIfpC1l1bQ/X.g5eEibaa")
				.roles("USER", "ADMIN")
				.build();

		return new CustomInMemoryUserDetailsManager(user, admin);
	}
}
