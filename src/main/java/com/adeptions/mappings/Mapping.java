package com.adeptions.mappings;

import com.adeptions.exceptions.MethodNotAllowedException;
import com.adeptions.exceptions.NasapiException;
import com.adeptions.wrappers.EndpointRequest;
import com.adeptions.wrappers.EndpointResponse;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.Undefined;

import javax.ws.rs.core.HttpHeaders;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Mapping {
	public static final String METHOD_OPTIONS = "OPTIONS";
	public static final Set<String> METHODS_SUPPORTED = new HashSet<String>();{{
		METHODS_SUPPORTED.add("GET");
		METHODS_SUPPORTED.add("POST");
		METHODS_SUPPORTED.add("PUT");
		METHODS_SUPPORTED.add("PATCH");
		METHODS_SUPPORTED.add("DELETE");
		METHODS_SUPPORTED.add("COPY");
		METHODS_SUPPORTED.add("HEAD");
		METHODS_SUPPORTED.add("OPTIONS");
		METHODS_SUPPORTED.add("LINK");
		METHODS_SUPPORTED.add("UNLINK");
		METHODS_SUPPORTED.add("PURGE");
		METHODS_SUPPORTED.add("LOCK");
		METHODS_SUPPORTED.add("UNLOCK");
		METHODS_SUPPORTED.add("PROPFIND");
		METHODS_SUPPORTED.add("VIEW");
	}};

	final String path;
	Map<String,ScriptObjectMirror> methods = new ConcurrentHashMap<String,ScriptObjectMirror>();
	String allowedMethods;

	public Mapping(String path, Map<String,Object> methods) {
		this.path = path;
		if (methods != null) {
			String methodName;
			Object methodObj;
			ScriptObjectMirror methodJsObject;
			for (Map.Entry<String, Object> entry : methods.entrySet()) {
				methodName = entry.getKey().toUpperCase();
				if (METHODS_SUPPORTED.contains(methodName)) {
					methodObj = entry.getValue();
					if (methodObj instanceof ScriptObjectMirror) {
						methodJsObject = (ScriptObjectMirror) methodObj;
						if (methodJsObject.isFunction()) {
							this.methods.put(methodName, methodJsObject);
						}
					}
				}
			}
		}
		buildAllowedMethods();
	}

	private void buildAllowedMethods() {
		StringBuilder builder = new StringBuilder(METHOD_OPTIONS);
		for (String method: methods.keySet()) {
			if (!method.equals(METHOD_OPTIONS)) {
				builder.append(",").append(method);
			}
		}
		allowedMethods = builder.toString();
	}

	public Object doMethod(String httpMethod,
						   String path,
						   EndpointRequest request,
						   EndpointResponse response) throws NasapiException {
		if (methods.containsKey(httpMethod)) {
			Object result = methods.get(httpMethod).call(this, request, response);
			if (result != null && result instanceof Undefined) {
				return null;
			}
			return result;
		} else if (METHOD_OPTIONS.equals(httpMethod)) {
			// build default options into response...
			response.setStatus(200);
			response.setHeader(HttpHeaders.ALLOW, allowedMethods);
			response.setBody(null);
		} else {
			throw new MethodNotAllowedException("URI '" + path + "' does not support method '" + httpMethod + "'", allowedMethods);
		}
		return null;
	}
}
