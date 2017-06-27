package com.adeptions.exceptions.mappers;

import com.adeptions.exceptions.NasapiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractExceptionMapper {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected Response buildResponse(NasapiException ex) {
		logger.warn(ex.getMessage());
		Map<String,Object> body = new HashMap<String,Object>();
		body.put("$error", ex.getMessage());
		return Response.status(ex.getStatusCode())
				.entity(body)
				.build();
	}
}
