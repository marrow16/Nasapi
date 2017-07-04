package com.adeptions.mappings;

import com.adeptions.exceptions.MappingException;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MappingTreeTests {
	@Test
	public void testRootEmpty() throws MappingException {
		List<PathToken> tokens = MappingTree.convertPathToTokens("/");
		assertEquals(0, tokens.size());
	}

	@Test
	public void testLeadingSlashIgnored() throws MappingException {
		List<PathToken> tokens = MappingTree.convertPathToTokens("/test");
		assertEquals(1, tokens.size());
	}

	@Test
	public void testTrailingSlashIgnored() throws MappingException {
		List<PathToken> tokens = MappingTree.convertPathToTokens("test/");
		assertEquals(1, tokens.size());
	}

	@Test
	public void testLeadingAndTrailingSlashIgnored() throws MappingException {
		List<PathToken> tokens = MappingTree.convertPathToTokens("/test/");
		assertEquals(1, tokens.size());
	}

	@Test
	public void testFixedPart() throws MappingException {
		List<PathToken> tokens = MappingTree.convertPathToTokens("test");
		assertEquals(1, tokens.size());
		assertEquals(true, tokens.get(0).isFixed);
	}

	@Test
	public void testVariablePart() throws MappingException {
		List<PathToken> tokens = MappingTree.convertPathToTokens("{name}");
		assertEquals(1, tokens.size());
		assertEquals(false, tokens.get(0).isFixed);
	}

	@Test
	public void testRootMapping() throws MappingException {
		Mapping mapping = new Mapping("/", null);
		MappingTree mappingTree = new MappingTree();
		mappingTree.put(mapping);
		assertEquals(mapping, mappingTree.rootMapping);
	}

	@Test
	public void testRootMappingOnlyOneAllowed() {
		boolean failed = false;
		try {
			MappingTree mappingTree = new MappingTree();
			mappingTree.put(new Mapping("/", null));
			mappingTree.put(new Mapping("/", null));
		} catch (MappingException me) {
			failed = true;
		}
		assertEquals(true, failed);
	}

	@Test
	public void testOneFixedVariablePath() throws MappingException {
		Mapping mapping = new Mapping("/fixed/{variable}", null);
		MappingTree mappingTree = new MappingTree();
		mappingTree.put(mapping);
		assertEquals(1, mappingTree.fixedEntryMappings.size());
		assertEquals(true, mappingTree.fixedEntryMappings.containsKey("fixed"));
		assertEquals(1, mappingTree.fixedEntryMappings.get("fixed").variableEntryMappings.size());
		assertEquals(true, mappingTree.fixedEntryMappings.get("fixed").variableEntryMappings.get(0).lastInPath);
		assertEquals(mapping, mappingTree.fixedEntryMappings.get("fixed").variableEntryMappings.get(0).mapping);
	}

	@Test
	public void testTwoFixedVariablePath() throws MappingException {
		Mapping mapping1 = new Mapping("/fixed/{variable1}", null);
		Mapping mapping2 = new Mapping("/fixed/{variable2}", null);
		MappingTree mappingTree = new MappingTree();
		mappingTree.put(mapping1);
		mappingTree.put(mapping2);
		assertEquals(1, mappingTree.fixedEntryMappings.size());
		assertEquals(true, mappingTree.fixedEntryMappings.containsKey("fixed"));
		assertEquals(2, mappingTree.fixedEntryMappings.get("fixed").variableEntryMappings.size());
		assertEquals(true, mappingTree.fixedEntryMappings.get("fixed").variableEntryMappings.get(0).lastInPath);
		assertEquals(mapping1, mappingTree.fixedEntryMappings.get("fixed").variableEntryMappings.get(0).mapping);
		assertEquals(mapping2, mappingTree.fixedEntryMappings.get("fixed").variableEntryMappings.get(1).mapping);
	}

	@Test
	public void testOneVariableFixedPath() throws MappingException {
		Mapping mapping = new Mapping("/{variable}/fixed", null);
		MappingTree mappingTree = new MappingTree();
		mappingTree.put(mapping);
		assertEquals(1, mappingTree.variableEntryMappings.size());
		assertEquals(1, mappingTree.variableEntryMappings.get(0).fixedEntryMappings.size());
		assertEquals(true, mappingTree.variableEntryMappings.get(0).fixedEntryMappings.containsKey("fixed"));
		assertEquals(true, mappingTree.variableEntryMappings.get(0).fixedEntryMappings.get("fixed").lastInPath);
		assertEquals(mapping, mappingTree.variableEntryMappings.get(0).fixedEntryMappings.get("fixed").mapping);
	}

	@Test
	public void testOneVariableFixedVariablePath() throws MappingException {
		Mapping mapping = new Mapping("{part1}/test/{part2}", null);
		MappingTree mappingTree = new MappingTree();
		mappingTree.put(mapping);
		assertEquals(1, mappingTree.variableEntryMappings.size());
		assertEquals(1, mappingTree.variableEntryMappings.get(0).fixedEntryMappings.size());
		assertEquals(true, mappingTree.variableEntryMappings.get(0).fixedEntryMappings.containsKey("test"));
		assertEquals(1, mappingTree.variableEntryMappings.get(0).fixedEntryMappings.get("test").variableEntryMappings.size());
		assertEquals(true, mappingTree.variableEntryMappings.get(0).fixedEntryMappings.get("test").variableEntryMappings.get(0).lastInPath);
		assertEquals(mapping, mappingTree.variableEntryMappings.get(0).fixedEntryMappings.get("test").variableEntryMappings.get(0).mapping);
	}

	@Test
	public void testFindFixedVariable() throws MappingException {
		Mapping mapping1 = new Mapping("/fixed/{variable: [a-zA-Z]+}", null);
		Mapping mapping2 = new Mapping("/fixed/{variable: \\d+}", null);
		MappingTree mappingTree = new MappingTree();
		mappingTree.put(mapping1);
		mappingTree.put(mapping2);
		MultivaluedHashMap<String,String> pathVariables1 = new MultivaluedHashMap<String,String>();
		MultivaluedHashMap<String,String> pathVariables2 = new MultivaluedHashMap<String,String>();
		Mapping find1 = mappingTree.find("fixed/aaa", pathVariables1);
		Mapping find2 = mappingTree.find("fixed/123", pathVariables2);
		Mapping find3 = mappingTree.find("fixed/");
		Mapping find4 = mappingTree.find("fixed");
		assertEquals(mapping1, find1);
		assertEquals(mapping2, find2);
		assertNull(find3);
		assertNull(find4);
		assertEquals(true, pathVariables1.containsKey("variable"));
		assertEquals(true, pathVariables2.containsKey("variable"));
		assertEquals(1, pathVariables1.get("variable").size());
		assertEquals(1, pathVariables2.get("variable").size());
		assertEquals("aaa", pathVariables1.get("variable").get(0));
		assertEquals("123", pathVariables2.get("variable").get(0));
	}

	@Test
	public void testComplexFixedAndVariable() throws MappingException {
		Mapping mapping1 = new Mapping("/database/{databaseName: [a-zA-Z]+}", null);
		Mapping mapping2 = new Mapping("/database/{databaseName: [a-zA-Z]+}/collection", null);
		Mapping mapping3 = new Mapping("/database/{databaseName: [a-zA-Z]+}/collection/{collectionName: [a-zA-Z]+}", null);
		Mapping mapping4 = new Mapping("/database/{databaseName: [a-zA-Z]+}/collection/{collectionName: [a-zA-Z]+}/items", null);
		Mapping mapping5 = new Mapping("/database/{databaseName: [a-zA-Z]+}/collection/{collectionName: [a-zA-Z]+}/items/{itemId: [0-9]+}", null);
		MappingTree mappingTree = new MappingTree();
		mappingTree.put(mapping1);
		mappingTree.put(mapping2);
		mappingTree.put(mapping3);
		mappingTree.put(mapping4);
		mappingTree.put(mapping5);
		MultivaluedHashMap<String,String> pathVariables1 = new MultivaluedHashMap<String,String>();
		MultivaluedHashMap<String,String> pathVariables2 = new MultivaluedHashMap<String,String>();
		MultivaluedHashMap<String,String> pathVariables3 = new MultivaluedHashMap<String,String>();
		MultivaluedHashMap<String,String> pathVariables4 = new MultivaluedHashMap<String,String>();
		MultivaluedHashMap<String,String> pathVariables5 = new MultivaluedHashMap<String,String>();
		Mapping find1 = mappingTree.find("database/foodb", pathVariables1);
		Mapping find2 = mappingTree.find("database/foodb/collection", pathVariables2);
		Mapping find3 = mappingTree.find("database/foodb/collection/foocollection", pathVariables3);
		Mapping find4 = mappingTree.find("database/foodb/collection/foocollection/items", pathVariables4);
		Mapping find5 = mappingTree.find("database/foodb/collection/foocollection/items/123", pathVariables5);
		assertEquals(mapping1, find1);
		assertEquals("foodb", pathVariables1.getFirst("databaseName"));
		assertEquals(mapping2, find2);
		assertEquals("foodb", pathVariables2.getFirst("databaseName"));
		assertEquals(mapping3, find3);
		assertEquals("foodb", pathVariables3.getFirst("databaseName"));
		assertEquals("foocollection", pathVariables3.getFirst("collectionName"));
		assertEquals(mapping4, find4);
		assertEquals("foodb", pathVariables4.getFirst("databaseName"));
		assertEquals("foocollection", pathVariables4.getFirst("collectionName"));
		assertEquals(mapping5, find5);
		assertEquals("foodb", pathVariables5.getFirst("databaseName"));
		assertEquals("foocollection", pathVariables5.getFirst("collectionName"));
		assertEquals("123", pathVariables5.getFirst("itemId"));
	}
}
