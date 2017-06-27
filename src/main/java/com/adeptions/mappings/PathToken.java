package com.adeptions.mappings;

import com.adeptions.exceptions.MappingException;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

class PathToken {
	private static final Pattern nameChecker = Pattern.compile("[A-Za-z][A-Za-z0-9\\-]+");
	String part;
	boolean isFixed;
	boolean hasRegexp;
	String regexp;
	Pattern pattern;
	String pathVariableName;

	PathToken(String part) throws MappingException {
		this.part = part;
		if (part.startsWith("{") && part.endsWith("}")) {
			isFixed = false;
			String stripped = part.substring(1, part.length() - 1);
			int colonAt;
			if ((colonAt = stripped.indexOf(':')) > -1) {
				hasRegexp = true;
				pathVariableName = stripped.substring(0, colonAt).trim();
				regexp = stripped.substring(colonAt + 1).trim();
				try {
					pattern = Pattern.compile(regexp);
				} catch (PatternSyntaxException pse) {
					throw new MappingException("Mapping path part '" + part + "' contains invalid regexp!", pse);
				}
			} else {
				pathVariableName = stripped;
			}
			if (!nameChecker.matcher(pathVariableName).matches()) {
				throw new MappingException("Path variable name '" + pathVariableName + "' is invalid");
			}
		} else {
			isFixed = true;
			if (part.contains("{") || part.contains("}")) {
				throw new MappingException("Path part cannot contain fixed and variable tokens");
			}
		}
	}
}
