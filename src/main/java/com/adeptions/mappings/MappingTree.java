package com.adeptions.mappings;

import com.adeptions.exceptions.MappingException;
import org.springframework.util.MultiValueMap;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class MappingTree extends MappingTreeEntry {
	Mapping rootMapping;
	Map<String,Mapping> fixedPathMappings = new ConcurrentHashMap<String,Mapping>();

	MappingTree() {
		//
	}

	void put(Mapping mapping) throws MappingException {
		List<PathToken> pathTokens = convertPathToTokens(mapping.path);
		if (pathTokens.size() == 0) {
			if (rootMapping != null) {
				throw new MappingException("Only one root mapping allowed");
			}
			rootMapping = mapping;
			fixedPathMappings.put("/", mapping);
		} else {
			String fixedPath = pathTokensToFixedPath(pathTokens);
			if (fixedPath != null) {
				if (fixedPathMappings.containsKey(fixedPath)) {
					throw new MappingException("Duplicate fixed path mapping '" + mapping.path + "'!");
				}
				fixedPathMappings.put(fixedPath, mapping);
			} else {
				nextDown(mapping, pathTokens, 0);
			}
		}
	}

	static String pathTokensToFixedPath(List<PathToken> pathTokens) {
		StringBuilder resultBuilder = new StringBuilder();
		for (PathToken pathToken: pathTokens) {
			if (!pathToken.isFixed) {
				return null;
			}
			resultBuilder.append("/").append(pathToken.part);
		}
		return resultBuilder.toString();
	}

	static List<PathToken> convertPathToTokens(String path) throws MappingException {
		// split the path into segments...
		List<String> parts = new LinkedList<String>(Arrays.asList((path.startsWith("/") ? path.substring(1) : path).split("/", -1)));
		// if the last part is empty ignore it...
		if (parts.size() > 0 && parts.get(parts.size() - 1).isEmpty()) {
			parts.remove(parts.size() - 1);
		}
		// check for empty parts...
		List<PathToken> result = new ArrayList<PathToken>();
		for (String part: parts) {
			if (part.isEmpty()) {
				throw new MappingException("Mapping path '" + path + "' cannot contain empty segments");
			}
			result.add(new PathToken(part));
		}
		return result;
	}

	public Mapping find(String path, @NotNull MultivaluedMap<String,String> pathParametersFound) {
		pathParametersFound.clear();
		// if the path contains a trailing slash ignore it...
		String usePath = (path.endsWith("/") ? path.substring(0,path.length() - 1) : path);
		Mapping result = fixedPathMappings.get((usePath.startsWith("/") ? "" : "/") + usePath);
		if (result != null) {
			return result;
		}
		// split the path into segments...
		return find(Arrays.asList((path.startsWith("/") ? path.substring(1) : path).split("/", -1)), 0, pathParametersFound);
	}
	public Mapping find(String path) {
		MultivaluedHashMap<String,String> pathVariablesFound = new MultivaluedHashMap<String,String>();
		return find(path, pathVariablesFound);
	}

	public void clear() {
		rootMapping = null;

	}
}
