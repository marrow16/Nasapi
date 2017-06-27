package com.adeptions.mappings.functions;

import com.adeptions.exceptions.MappingException;
import jdk.nashorn.api.scripting.NashornException;

import javax.script.ScriptException;

@FunctionalInterface
public interface BindFunction {
	void bind(String name, Object object) throws ScriptException, NashornException, MappingException;
}
