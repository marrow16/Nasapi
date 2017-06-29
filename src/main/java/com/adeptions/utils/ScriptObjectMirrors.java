package com.adeptions.utils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScriptObjectMirrors {
	public static Object convert(Object original) {
		if (original == null) {
			return null;
		} else if (original instanceof String || original instanceof Integer || original instanceof Long || original instanceof Boolean || original instanceof Double) {
			return original;
		} else if (original instanceof ScriptObjectMirror) {
			ScriptObjectMirror jsOriginal = (ScriptObjectMirror)original;
			if (jsOriginal.isArray()) {
				List<Object> listResult = new ArrayList<Object>();
				Integer length = (Integer)jsOriginal.get("length");
				for (int i = 0; i < length; i++) {
					listResult.add(convert(jsOriginal.get("" + i)));
				}
				return listResult;
			} else if (jsOriginal.isFunction()) {
				// can't convert it...
				return null;
			}
			Map<String,Object> mapResult = new LinkedHashMap<String,Object>();
			for (Map.Entry<String,Object> entry: jsOriginal.entrySet()) {
				mapResult.put(entry.getKey(), convert(entry.getValue()));
			}
			return mapResult;
		}
		return original;
	}

	public static String Stringify(Object scriptObject) {
		StringBuilder builder = new StringBuilder();
		StringifyObject(scriptObject, builder, false);
		return builder.toString();
	}

	private static void StringifyObject(Object object, StringBuilder builder, boolean inObjectOrArray) {
		if (object == null) {
			builder.append("null");
		} else if (object instanceof String && inObjectOrArray) {
			builder.append("\"").append(((String) object).replaceAll("\"", "\\\"")).append("\"");
		} else if (object instanceof String) {
			builder.append(object);
		} else if (object instanceof Integer || object instanceof Long || object instanceof Boolean || object instanceof Double) {
			builder.append(object.toString());
		} else if (object instanceof ScriptObjectMirror) {
			ScriptObjectMirror scriptObject = (ScriptObjectMirror)object;
			if (scriptObject.isArray()) {
				builder.append("[");
				Integer length = (Integer) scriptObject.get("length");
				for (int i = 0; i < length; i++) {
					StringifyObject(scriptObject.get("" + i), builder, true);
				}
				builder.append("]");
			} else if (scriptObject.isFunction()) {
				builder.append("[function()]");
			} else {
				builder.append("{");
				boolean isFirst = true;
				for (Map.Entry<String, Object> entry : scriptObject.entrySet()) {
					builder.append(isFirst ? "" : ", ")
							.append("\"")
							.append(entry.getKey())
							.append("\": ");
					StringifyObject(entry.getValue(), builder, true);
					isFirst = false;
				}
				builder.append("}");
			}
		} else {
			builder.append(object.toString());
		}
	}
}
