package com.adeptions.functions;

import com.adeptions.exceptions.NasapiException;
import jdk.nashorn.api.scripting.NashornException;

import javax.script.ScriptException;

@FunctionalInterface
public interface RegisterAuthenticationFunction {
	void registerAuthentication(Object authenticationFunction) throws ScriptException, NashornException, NasapiException;
}
