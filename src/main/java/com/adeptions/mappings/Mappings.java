package com.adeptions.mappings;

import com.adeptions.exceptions.MappingException;
import com.adeptions.functions.RegisterMappingFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;

public class Mappings implements RegisterMappingFunction {
	public static final String FUNCTION_NAME_REGISTER_MAPPING = "registerMapping";

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private MappingTree mappingTree;

	public Mappings() {
		init();
	}

	private void init() {
		logger.info("Initialising endpoint mappings");
		mappingTree = new MappingTree();
	}

	public Mapping find(String path, @NotNull MultivaluedMap<String,String> pathParametersFound) {
		return mappingTree.find(path, pathParametersFound);
	}

	public Mapping find(String path) {
		return mappingTree.find(path);
	}

	@Override
	public Mapping registerMapping(String path, Map<String,Object> methods) throws MappingException {
		logger.info("registerMapping - path '" + path + "'");
		Mapping result = new Mapping(path, methods);
		mappingTree.put(result);
		return result;
	}

	public void clear() {
		mappingTree = new MappingTree();
	}
}
