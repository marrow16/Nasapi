package com.adeptions.writers;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

public abstract class AbstractXmlWriter {
	private static String XML_NAME_CHARS_REGEX = "[^A-Z_a-z\\u00C0\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02ff\\u0370-\\u037d"
			+ "\\u037f-\\u1fff\\u200c\\u200d\\u2070-\\u218f\\u2c00-\\u2fef\\u3001-\\ud7ff"
			+ "\\uf900-\\ufdcf\\ufdf0-\\ufffd\\x10000-\\xEFFFF]"
			+ "[^A-Z_a-z\\u00C0\\u00D6\\u00D8-\\u00F6"
			+ "\\u00F8-\\u02ff\\u0370-\\u037d\\u037f-\\u1fff\\u200c\\u200d\\u2070-\\u218f"
			+ "\\u2c00-\\u2fef\\u3001-\\udfff\\uf900-\\ufdcf\\ufdf0-\\ufffd\\-\\.0-9"
			+ "\\u00b7\\u0300-\\u036f\\u203f-\\u2040]*";

	protected void writeValue(Map<String,Object> object, XMLStreamWriter writer) throws IOException, XMLStreamException {
		for (Map.Entry<String,Object> entry: object.entrySet()) {
			writer.writeStartElement(entry.getKey().replaceAll(XML_NAME_CHARS_REGEX, "_"));
			writeValue(entry.getValue(), writer);
			writer.writeEndElement();
		}
	}

	protected void writeValue(List<Object> object, XMLStreamWriter writer) throws IOException, XMLStreamException {
		writer.writeStartElement("items");
		for (Object item: object) {
			writer.writeStartElement("item");
			writeValue(item, writer);
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}

	protected void writeValue(Object object, XMLStreamWriter writer) throws IOException, XMLStreamException {
		if (object != null) {
			if (object instanceof Map) {
				writeValue((Map)object, writer);
			} else if (object instanceof List) {
				writeValue((List)object, writer);
			} else {
				writer.writeCharacters(object.toString());
			}
		}
	}
}
