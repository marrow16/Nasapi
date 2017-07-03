package com.adeptions.exceptions.mappers;

import com.adeptions.exceptions.MethodNotAllowedException;
import com.adeptions.exceptions.mappers.AbstractExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class MethodNotAllowedExceptionMapper extends AbstractExceptionMapper implements ExceptionMapper<MethodNotAllowedException> {
	private static final Logger logger = LoggerFactory.getLogger(MethodNotAllowedException.class);
	@Context
	HttpServletRequest request;

	@Produces(MediaType.APPLICATION_JSON)
	public Response toResponse(MethodNotAllowedException ex) {
		logger.warn(ex.getMessage() + buildRequestInfoMessage(request));
		Map<String,Object> body = new HashMap<String,Object>();
		body.put("$error", ex.getMessage());
		return Response.status(ex.getStatusCode())
				.header(HttpHeaders.ALLOW, ex.getAllowedMethods())
				.entity(body)
				.build();
	}
}
