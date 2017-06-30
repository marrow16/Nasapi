package com.adeptions.engine;

import com.adeptions.exceptions.MappingException;
import com.adeptions.functions.ReloadFunction;
import jdk.nashorn.api.scripting.NashornException;

import javax.script.ScriptException;
import java.io.IOException;

public class ReloadFunctionImpl implements ReloadFunction {
	private NashornScriptEngineHolder scriptEngineHolder;

	ReloadFunctionImpl(NashornScriptEngineHolder scriptEngineHolder) {
		this.scriptEngineHolder = scriptEngineHolder;
	}

	@Override
	public void reload() throws NashornException, MappingException, ScriptException, IOException {
		scriptEngineHolder.reload();
	}
}
