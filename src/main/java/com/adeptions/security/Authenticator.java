package com.adeptions.security;

import com.adeptions.engine.NashornScriptEngineHolder;
import com.adeptions.exceptions.NasapiException;
import com.adeptions.functions.RegisterAuthenticationFunction;
import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptException;
import java.util.Map;

public class Authenticator implements RegisterAuthenticationFunction {
	public static final String FUNCTION_NAME_REGISTER_AUTHENTICATION = "registerAuthenticator";

	private NashornScriptEngineHolder scriptEngineHolder;
	private ScriptObjectMirror authenticateFunction = null;

	public Authenticator(NashornScriptEngineHolder scriptEngineHolder) {
		this.scriptEngineHolder = scriptEngineHolder;
	}

	public AuthenticationResponse authenticate(String username) {
		AuthenticationResponse result = null;
		if (authenticateFunction != null) {
			result = new AuthenticationResponse(username);
			Object callResult = authenticateFunction.call(this, username, result);
			if (callResult != null) {
				if (callResult instanceof String) {
					result.setPassword((String)callResult);
				} else if (callResult instanceof AuthenticationResponse) {
					result = (AuthenticationResponse)callResult;
				} else if (callResult instanceof Map) {
					result.populateFromMap((Map<String,Object>)callResult);
				} else {
					result = null;
				}
			} else {
				result = null;
			}
		} else if ("admin".equals(username)) {
			result = new AuthenticationResponse(username);
			result.setPassword("admin");
		}
		return result;
	}

	@Override
	public void registerAuthentication(Object registerFunction) throws ScriptException, NashornException, NasapiException {
		if (registerFunction == null) {
			throw new NasapiException("registerAuthentication argument cannot be null");
		} else if (registerFunction instanceof ScriptObjectMirror && ((ScriptObjectMirror)registerFunction).isFunction()) {
			authenticateFunction = (ScriptObjectMirror)registerFunction;
		} else {
			throw new NasapiException("registerAuthentication argument must be a function");
		}
	}
}
