package com.adeptions.mappings;

import com.adeptions.exceptions.MappingException;
import org.junit.Test;

import java.util.regex.PatternSyntaxException;

import static org.junit.Assert.assertEquals;

public class PathTokenTests {
	private String goodRegexp = "[a-zA-Z]+";
	private String badRegexp = "***";

	@Test
	public void testInterpretationFixed() throws MappingException {
		PathToken pathToken = new PathToken("fixed");
		assertEquals(true, pathToken.isFixed);
	}

	@Test
	public void testInterpretationVariable() throws MappingException {
		PathToken pathToken = new PathToken("{name}");
		assertEquals(false, pathToken.isFixed);
		assertEquals("name", pathToken.pathVariableName);
	}

	@Test
	public void testInterpretationVariableBadName() {
		boolean failed = false;
		try {
			new PathToken("{name???}");
		} catch (MappingException me) {
			failed = true;
		}
		assertEquals(true, failed);
	}

	@Test
	public void testInterpretationMixFails() {
		boolean failed = false;
		try {
			new PathToken("foo{name}");
		} catch (MappingException me) {
			failed = true;
		}
		assertEquals(true, failed);
	}

	@Test
	public void testInterpretationVariableAndRegexp() throws MappingException {
		PathToken pathToken = new PathToken("{name: " + goodRegexp + "}");
		assertEquals(false, pathToken.isFixed);
		assertEquals("name", pathToken.pathVariableName);
		assertEquals(true, pathToken.hasRegexp);
		assertEquals(goodRegexp, pathToken.regexp);
	}

	@Test
	public void testInterpretationVariableAndRegexpExtraSpaces() throws MappingException {
		PathToken pathToken = new PathToken("{name:    " + goodRegexp + "   }");
		assertEquals(false, pathToken.isFixed);
		assertEquals(goodRegexp, pathToken.regexp);
	}

	@Test
	public void testInterpretationVariableExtraSpacesAndRegexp() throws MappingException {
		PathToken pathToken = new PathToken("{  name  : " + goodRegexp + "}");
		assertEquals(false, pathToken.isFixed);
		assertEquals("name", pathToken.pathVariableName);
	}

	@Test
	public void testInterpretationVariableAndBadRegexpFails() {
		boolean failed = false;
		try {
			new PathToken("{name: " + badRegexp + "}");
		} catch (MappingException me) {
			failed = true;
		}
		assertEquals(true, failed);
	}
}
