package com.adeptions.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class AuthenticationResponse {
	private String username;
	private String password;
	private Boolean enabled = true;
	private Boolean accountExpired = false;
	private Boolean credentialsExpired = false;
	private Boolean accountLocked = false;
	private List<String> roles = new ArrayList<String>();

	public AuthenticationResponse(String username) {
		this.username = username;
		roles.add("USER");
	}

	public void populateFromMap(Map<String,Object> map) {
		if (map != null) {
			if (map.containsKey("username") && map.get("username") instanceof String) {
				username = (String)map.get("username");
			}
			if (map.containsKey("password") && map.get("password") instanceof String) {
				password = (String)map.get("password");
			}
			if (map.containsKey("enabled") && map.get("enabled") instanceof Boolean) {
				enabled = (Boolean)map.get("enabled");
			}
			if (map.containsKey("accountExpired") && map.get("accountExpired") instanceof Boolean) {
				accountExpired = (Boolean)map.get("accountExpired");
			}
			if (map.containsKey("credentialsExpired") && map.get("credentialsExpired") instanceof Boolean) {
				credentialsExpired = (Boolean)map.get("credentialsExpired");
			}
			if (map.containsKey("accountLocked") && map.get("accountLocked") instanceof Boolean) {
				accountLocked = (Boolean)map.get("accountLocked");
			}
			if (map.containsKey("roles") && map.get("roles") instanceof List) {
				roles.clear();;
				List<Object> rolesList = (List<Object>)map.get("roles");
				for (Object role: rolesList) {
					if (role instanceof String) {
						roles.add((String)role);
					}
				}
			}
		}
	}

	public User getUser() {
		return new User(username, password,
				enabled, !accountExpired, !credentialsExpired, !accountLocked,
				AuthorityUtils.createAuthorityList(roles.toArray(new String[roles.size()])));
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getAccountExpired() {
		return accountExpired;
	}
	public void setAccountExpired(Boolean accountExpired) {
		this.accountExpired = accountExpired;
	}

	public Boolean getCredentialsExpired() {
		return credentialsExpired;
	}
	public void setCredentialsExpired(Boolean credentialsExpired) {
		this.credentialsExpired = credentialsExpired;
	}

	public Boolean getAccountLocked() {
		return accountLocked;
	}
	public void setAccountLocked(Boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	public List<String> getRoles() {
		return roles;
	}
}
