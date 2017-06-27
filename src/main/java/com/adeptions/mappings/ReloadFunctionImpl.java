package com.adeptions.mappings;

import com.adeptions.exceptions.MappingException;
import com.adeptions.mappings.functions.ReloadFunction;
import jdk.nashorn.api.scripting.NashornException;

import javax.script.ScriptException;
import java.io.IOException;

public class ReloadFunctionImpl implements ReloadFunction {
	private Mappings mappings;

	ReloadFunctionImpl(Mappings mappings) {
		this.mappings = mappings;
	}

	@Override
	public void reload() throws NashornException, MappingException, ScriptException, IOException {
		mappings.reload();
	}
}
