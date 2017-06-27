package com.adeptions.mappings.functions;

import com.adeptions.exceptions.MappingException;
import jdk.nashorn.api.scripting.NashornException;

import javax.script.ScriptException;
import java.io.IOException;

@FunctionalInterface
public interface ReloadFunction {
	void reload() throws NashornException, MappingException, ScriptException, IOException;
}
