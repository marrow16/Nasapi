package com.adeptions.exceptions.mappers;

import com.adeptions.exceptions.NasapiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class DefaultExceptionMapper extends AbstractExceptionMapper implements ExceptionMapper<Throwable> {
	@Context
	HttpServletRequest request;

	@Produces(MediaType.APPLICATION_JSON)
	public Response toResponse(Throwable ex) {
		Object cause = ex.getCause();
		if (cause != null && cause instanceof NasapiException) {
			return buildResponse((NasapiException)cause, null, request);
		}
		LoggerFactory.getLogger(ex.getClass()).warn(ex.getMessage() + buildRequestInfoMessage(request));
		Map<String,Object> body = new HashMap<String,Object>();
		body.put("$error", ex.getMessage());
		return Response.status(500).entity(body).build();
	}
}