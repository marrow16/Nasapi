package com.adeptions.exceptions.mappers;

import com.adeptions.exceptions.*;
import jdk.nashorn.internal.runtime.ECMAException;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractExceptionMapper {
	Response buildResponse(NasapiException ex, ECMAException outerScriptException, HttpServletRequest request) {
		LoggerFactory.getLogger(ex.getClass()).warn(ex.getMessage() + buildScriptInfoMessage(outerScriptException) + buildRequestInfoMessage(request));
		Map<String,Object> body = new HashMap<String,Object>();
		body.put("$error", ex.getMessage());
		return Response.status(ex.getStatusCode())
				.entity(body)
				.build();
	}

	String buildScriptInfoMessage(ECMAException outerScriptException) {
		String result = "";
		if (outerScriptException != null) {
			result = " [at:- " + outerScriptException.getFileName() + ":" + outerScriptException.getLineNumber() + "," + outerScriptException.getColumnNumber() + "]";
		}
		return result;
	}

	String buildRequestInfoMessage(HttpServletRequest request) {
		String result = "";
		if (request != null) {
			result = " [from:- " + request.getMethod() + " " + request.getRequestURI() + "]";
		}
		return result;
	}
}
