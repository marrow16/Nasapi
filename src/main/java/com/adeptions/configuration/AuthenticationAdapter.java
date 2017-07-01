package com.adeptions.configuration;

import com.adeptions.engine.NashornScriptEngineHolder;
import com.adeptions.security.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.BootGlobalAuthenticationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class AuthenticationAdapter extends BootGlobalAuthenticationConfiguration {
	@Autowired
	//	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
	}

	@Autowired
	NashornScriptEngineHolder scriptEngineHolder;

	@Bean
	UserDetailsService userDetailsService() {
		return new UserDetailsService() {

			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				AuthenticationResponse response = scriptEngineHolder.authenticate(username);
				if (response != null) {
					return response.getUser();
				} else {
					throw new UsernameNotFoundException("No such user '" + username + "'");
				}
/*
				//Account account = accountRepository.findByUsername(username);
//				if(account != null) {
					return new User(username, "admin", true, true, true, true,
							AuthorityUtils.createAuthorityList("USER"));
//				} else {
//					throw new UsernameNotFoundException("could not find the user '" + username + "'");
//				}
*/
			}

		};
	}}
