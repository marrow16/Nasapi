package com.adeptions.exceptions.mappers;

import com.adeptions.exceptions.NasapiException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

//@Provider
@Produces(MediaType.APPLICATION_JSON)
public class NasapiExceptionMapper extends AbstractExceptionMapper implements ExceptionMapper<NasapiException> {
	@Produces(MediaType.APPLICATION_JSON)
	public Response toResponse(NasapiException ex) {
		return buildResponse(ex);
	}
}
