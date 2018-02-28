package lt.vtvpmc.ems.isveikata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.google.common.collect.ImmutableList;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private SecurityEntryPoint securityEntryPoint;

	@Autowired
	private UserDetailsService userDetails;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
				// be saugumo UI dalis ir swaggeris
				.antMatchers("/", "/swagger-ui.html").permitAll()
				// visi /api/ saugus (dar galima .anyRequest() )
				.antMatchers("/api/**").authenticated().and().formLogin() // leidziam login
				// prisijungus
				.successHandler((req, res, auth) -> { // Success handler invoked after successful authentication
					String userRoles = "";
					for (GrantedAuthority authority : auth.getAuthorities()) {
						if (userRoles.length() > 0)
							userRoles = userRoles + ";";
						userRoles = userRoles + authority.getAuthority();
					}
					res.addHeader("Content-Type", "application/json; charset=utf-8x");
					res.getWriter().print("{\"fullName\":\"" + auth.getName() + "\",\"role\":\"" + userRoles + "\"}");
					res.getWriter().flush();
					// res.addCookie(new Cookie("userName", auth.getName()));
					// res.addHeader("userName", auth.getName());
					// res.sendRedirect("/"); // Redirect user to index/home page
				})
				// .successHandler(new SimpleUrlAuthenticationSuccessHandler())
				// esant blogiems user/pass
				.failureHandler(new SimpleUrlAuthenticationFailureHandler())
				.loginPage("/api/login").permitAll() // jis	jau egzistuoja!
				.usernameParameter("userName").passwordParameter("password").and().logout()
				// .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.permitAll() // leidziam /logout
				.and().csrf().disable() // nenaudojam tokenu
				// toliau forbidden klaidai
				.exceptionHandling().authenticationEntryPoint(securityEntryPoint).and().headers().frameOptions()
				.disable() // H2 konsolei
				.and().cors();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(ImmutableList.of("*"));
		configuration.setAllowedMethods(ImmutableList.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
		// setAllowCredentials(true) is important, otherwise:
		// The value of the 'Access-Control-Allow-Origin' header in the response must
		// not be the wildcard '*' when the request's credentials mode is 'include'.
		configuration.setAllowCredentials(true);
		// setAllowedHeaders is important! Without it, OPTIONS preflight request
		// will fail with 403 Invalid CORS request
		configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new SHA256Encrypt();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetails).passwordEncoder(passwordEncoder());
	}
}