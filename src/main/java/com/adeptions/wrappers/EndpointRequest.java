package com.adeptions.wrappers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EndpointRequest {
	private UriInfo uriInfo;
	private HttpServletRequest httpRequest;
	private MultivaluedMap<String,String> pathParameters;
	private InputStream body;
	private boolean gotBodyString = false;
	private String bodyString;
	private Authentication authentication;
	private Set<String> roles = new HashSet<String>();

	public EndpointRequest(UriInfo uriInfo,
						   HttpServletRequest httpRequest,
						   MultivaluedMap<String,String> pathParameters,
						   InputStream body,
						   Authentication authentication) {
		this.uriInfo = uriInfo;
		this.httpRequest = httpRequest;
		this.pathParameters = pathParameters;
		this.body = body;
		this.authentication = authentication;
		if (authentication != null) {
			for (GrantedAuthority authority: authentication.getAuthorities()) {
				roles.add(authority.getAuthority());
			}
		}
	}

	public UriInfo getUriInfo() {
		return uriInfo;
	}

	public HttpServletRequest getHttpRequest() {
		return httpRequest;
	}

	public MultivaluedMap<String, String> getPathParameters() {
		return pathParameters;
	}

	public List<String> getPathParameter(String name) {
		return pathParameters.get(name);
	}

	public String getPathParameterFirst(String name) {
		return pathParameters.getFirst(name);
	}

	public MultivaluedMap<String, String> getQueryParameters() {
		return uriInfo.getQueryParameters();
	}

	public List<String> getQueryParameter(String name) {
		return uriInfo.getQueryParameters().get(name);
	}

	public String getQueryParameterFirst(String name) {
		return uriInfo.getQueryParameters().getFirst(name);
	}

	public String getHeader(String name) {
		return httpRequest.getHeader(name);
	}

	public InputStream getBody() {
		return body;
	}

	public String getBodyString() throws IOException {
		if (!gotBodyString) {
			final int bufferSize = 1024;
			final char[] buffer = new char[bufferSize];
			final StringBuilder out = new StringBuilder();
			Reader in = new InputStreamReader(body, "UTF-8");
			for (; ; ) {
				int rsz = in.read(buffer, 0, bufferSize);
				if (rsz < 0)
					break;
				out.append(buffer, 0, rsz);
			}
			bodyString = out.toString();
		}
		return bodyString;
	}

	public Authentication getAuthentication() {
		return authentication;
	}

	public Boolean hasRole(String role) {
		return roles.contains(role);
	}
}
