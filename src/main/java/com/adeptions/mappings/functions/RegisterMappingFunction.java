package com.adeptions.mappings.functions;

import com.adeptions.exceptions.MappingException;
import com.adeptions.mappings.Mapping;
import jdk.nashorn.api.scripting.NashornException;

import javax.script.ScriptException;
import java.util.Map;

@FunctionalInterface
public interface RegisterMappingFunction {
	Mapping registerMapping(String path, Map<String,Object> methods) throws ScriptException, NashornException, NoSuchMethodException, MappingException;
}
