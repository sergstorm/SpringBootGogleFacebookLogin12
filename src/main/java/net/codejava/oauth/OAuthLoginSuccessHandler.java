package net.codejava.oauth;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.codejava.entity.AuthenticationType;
import net.codejava.entity.Role;
import net.codejava.entity.User;
import net.codejava.repository.UserRepo;
import net.codejava.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuthLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired UserSevice userService;
	@Autowired
	UserRepo repository;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
		String oauth2ClientName = oauth2User.getOauth2ClientName();
		String email = oauth2User.getEmail();
		String username = oauth2User.getName();
		//System.out.println("EMAIL+"+username+"OATHUSER"+oauth2User.getEmail()+oauth2User.getName()+oauth2User.getAttribute("email")+" iu");

		AuthenticationType authType = AuthenticationType.valueOf(oauth2ClientName.toUpperCase());
		System.out.println(authType+" AUTH __  TYPE");
        User userfd = repository.getUserByUsername(oauth2User.getName());
		if (userfd==null)
		{
			User user = new User();
			user.setActive(true);
			user.setUsername(oauth2User.getName());
			user.setEmail(email);
			user.setAuthType(authType);
			user.setRoles(Collections.singleton(new Role("USER")));
			user.setPassword("1");
			user.setActivationCode(null);
			Date date = new Date(System.currentTimeMillis());
			user.setRegDate(date);
			repository.save(user);
		}
		userService.updateAuthenticationType(username, oauth2ClientName);
		
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
