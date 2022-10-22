package net.codejava.security;

import net.codejava.service.UserSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import net.codejava.oauth.CustomOAuth2UserService;
import net.codejava.oauth.OAuthLoginSuccessHandler;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
		
		return authProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}
	@Override
	public void configure(WebSecurity web) {
		web.ignoring()
				.antMatchers("/resources/**", "/static/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/", "/login3","/reg","/static/**","/shop").permitAll()
			.anyRequest().authenticated()
			.and()

//				.formLogin()
//				.loginPage("/login2")
//				.  permitAll().defaultSuccessUrl("/main",true)

			.formLogin()
				.loginPage("/login3").permitAll().defaultSuccessUrl("/",true)
//				.usernameParameter("username")
//				.passwordParameter("password")
				.successHandler(databaseLoginSuccessHandler)
			.and()
			.oauth2Login()
				.loginPage("/login3")
				.userInfoEndpoint()
					.userService(oauth2UserService)
				.and()
				.successHandler(oauthLoginSuccessHandler)
			.and()
			.logout().logoutSuccessUrl("/").permitAll()
			.and()
				.rememberMe()
				.and()
			.exceptionHandling().accessDeniedPage("/403")
			;
	}
	
	@Autowired
	private CustomOAuth2UserService oauth2UserService;
	
	@Autowired
	private OAuthLoginSuccessHandler oauthLoginSuccessHandler;
	
	@Autowired
	private DatabaseLoginSuccessHandler databaseLoginSuccessHandler;
}
