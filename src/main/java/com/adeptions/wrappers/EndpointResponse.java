package com.adeptions.wrappers;

import com.adeptions.utils.ScriptObjectMirrors;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class EndpointResponse {
	private HttpServletResponse httpResponse;
	private Integer status = 200;
	boolean bodySet;
	private Object body;
	private Map<String, String> headers = new HashMap<String, String>();

	public EndpointResponse(HttpServletResponse httpResponse) {
		this.httpResponse = httpResponse;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeader(String name, String value) {
		headers.put(name, value);
	}

	public String getHeader(String name) {
		return headers.get(name);
	}

	public HttpServletResponse getHttpResponse() {
		return httpResponse;
	}

	public void setBody(Object body) {
		bodySet = true;
		this.body = body;
	}

	public Response toResponse() {
		Response.ResponseBuilder builder = Response.status(status);
		if (bodySet && body != null) {
			builder.entity(ScriptObjectMirrors.convert(body));
		}
		setResponseBuilderHeaders(builder);
		return builder.build();
	}

	public Response toResponse(Object body) {
		Response.ResponseBuilder builder = Response.status(status)
				.entity(ScriptObjectMirrors.convert(body));
		setResponseBuilderHeaders(builder);
		return builder.build();
	}

	private void setResponseBuilderHeaders(Response.ResponseBuilder builder) {
		for (Map.Entry<String,String> header: headers.entrySet()) {
			builder.header(header.getKey(), header.getValue());
		}
	}
}
