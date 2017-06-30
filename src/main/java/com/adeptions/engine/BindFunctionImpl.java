package com.adeptions.engine;

import com.adeptions.exceptions.MappingException;
import com.adeptions.functions.BindFunction;
import jdk.nashorn.api.scripting.NashornException;

import javax.script.ScriptException;

public class BindFunctionImpl implements BindFunction {
	private NashornScriptEngineHolder scriptEngineHolder;

	BindFunctionImpl(NashornScriptEngineHolder scriptEngineHolder) {
		this.scriptEngineHolder = scriptEngineHolder;
	}

	@Override
	public void bind(String name, Object object) throws ScriptException, NashornException, MappingException {
		scriptEngineHolder.bind(name, object);
	}
}
