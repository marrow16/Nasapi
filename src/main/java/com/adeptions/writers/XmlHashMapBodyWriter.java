package com.adeptions.writers;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;

@Provider
@Produces(MediaType.APPLICATION_XML)
public class XmlHashMapBodyWriter extends AbstractXmlWriter implements MessageBodyWriter<HashMap> {
	@Override
	public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public long getSize(HashMap hashMap, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(HashMap hashMap, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
		try {
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(outputStream);
			xmlWriter.writeStartElement("entity");
			writeValue(hashMap, xmlWriter);
			xmlWriter.writeEndElement();
			xmlWriter.flush();
			xmlWriter.close();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}
}
