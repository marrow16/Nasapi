package com.adeptions.mappings;

import com.adeptions.exceptions.MappingException;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class MappingTreeEntry {
	boolean lastInPath;
	Mapping mapping;
	PathToken pathToken;
	Map<String, MappingTreeEntry> fixedEntryMappings = new ConcurrentHashMap<String,MappingTreeEntry>();
	List<MappingTreeEntry> variableEntryMappings = Collections.synchronizedList(new ArrayList<MappingTreeEntry>());

	MappingTreeEntry() {

	}

	MappingTreeEntry(Mapping mapping, List<PathToken> pathTokens, int index) throws MappingException {
		add(mapping, pathTokens, index);
	}

	void add(Mapping mapping, List<PathToken> pathTokens, int index) throws MappingException {
		this.pathToken = pathTokens.get(index);
		lastInPath = (index == (pathTokens.size() - 1));
		if (!lastInPath) {
			nextDown(mapping, pathTokens, index + 1);
		} else {
			this.mapping = mapping;
		}
	}

	void nextDown(Mapping mapping, List<PathToken> pathTokens, int index) throws MappingException {
		PathToken nextDown = pathTokens.get(index);
		if (nextDown.isFixed) {
			MappingTreeEntry existingEntry = fixedEntryMappings.get(nextDown.part);
			if (existingEntry != null) {
				// add to end of existing...
				// but are they both last in their path...
				boolean nextIsLast = (index == (pathTokens.size() - 1));
				if (nextIsLast && existingEntry.lastInPath) {
					throw new MappingException("Duplicate mapping entry on '" + tokensToPathString(pathTokens) + "'");
				} else if (nextIsLast) {
					existingEntry.lastInPath = true;
					existingEntry.mapping = mapping;
				} else {
					existingEntry.nextDown(mapping, pathTokens, index + 1);
				}
			} else {
				fixedEntryMappings.put(nextDown.part, new MappingTreeEntry(mapping, pathTokens, index));
			}
		} else {
			variableEntryMappings.add(new MappingTreeEntry(mapping, pathTokens, index));
		}
	}

	protected Mapping find(List<String> pathParts, int index, @NotNull MultivaluedMap<String,String> pathVariablesFound) {
		String partToFind = pathParts.get(index);
		boolean onLastPart = (index == (pathParts.size() - 1));
		MappingTreeEntry found = fixedEntryMappings.get(partToFind);
		if (found != null) {
			if (found.lastInPath && onLastPart) {
				return found.mapping;
			} else if (!onLastPart) {
				return found.find(pathParts, index + 1, pathVariablesFound);
			}
		}
		Mapping foundMapping;
		for (MappingTreeEntry entry: variableEntryMappings) {
			if (!entry.pathToken.hasRegexp || entry.pathToken.pattern.matcher(partToFind).matches()) {
				// it's a candidate...
				if (entry.lastInPath && onLastPart) {
					pathVariablesFound.add(entry.pathToken.pathVariableName, partToFind);
					return entry.mapping;
				} else if (!entry.lastInPath && !onLastPart) {
					foundMapping = entry.find(pathParts, index + 1, pathVariablesFound);
					if (foundMapping != null) {
						pathVariablesFound.add(entry.pathToken.pathVariableName, partToFind);
						return foundMapping;
					}
				}
			}
		}
		return null;
	}

	private static String tokensToPathString(List<PathToken> pathTokens) {
		StringBuilder builder = new StringBuilder();
		pathTokens.forEach(pathToken -> {
			builder.append("/").append(pathToken);
		});
		return builder.toString();
	}
}
