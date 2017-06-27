package com.adeptions.mappings;

import com.adeptions.exceptions.MappingException;
import com.adeptions.mappings.functions.BindFunction;
import jdk.nashorn.api.scripting.NashornException;

import javax.script.ScriptException;

public class BindFunctionImpl implements BindFunction {
	private Mappings mappings;

	BindFunctionImpl(Mappings mappings) {
		this.mappings = mappings;
	}

	@Override
	public void bind(String name, Object object) throws ScriptException, NashornException, MappingException {
		mappings.bind(name, object);
	}
}
