package com.adeptions.exceptions.mappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class DefaultExceptionMapper implements ExceptionMapper<Exception> {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Produces(MediaType.APPLICATION_JSON)
	public Response toResponse(Exception ex) {
		logger.warn(ex.getMessage());
		Map<String,Object> body = new HashMap<String,Object>();
		body.put("$error", ex.getMessage());
		return Response.status(500).entity(body).build();
	}
}