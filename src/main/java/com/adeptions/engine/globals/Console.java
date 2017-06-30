package com.adeptions.engine.globals;

import com.adeptions.utils.ScriptObjectMirrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

public class Console {
	public static final String BINDING_NAME = "console";

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private int groupLevel;
	private Map<String,Long> timers = new HashMap<String,Long>();

	private String buildOutputMessage(Object... args) {
		StringBuilder builder = new StringBuilder();
		if (groupLevel > 0) {
			builder.append(CharBuffer.allocate(groupLevel).toString().replace('\0', '\t'));
		}
		for (Object arg: args) {
			builder.append(ScriptObjectMirrors.Stringify(arg)).append(" ");
//			if (arg == null) {
//				builder.append("null ");
//			} else {
//				builder.append(arg.toString()).append(" ");
//			}
		}
		return builder.toString();
	}

	public void debug(Object... args) {
		logger.debug(buildOutputMessage(args));
	}

	public void error(Object... args) {
		logger.error(buildOutputMessage(args));
	}

	public void exception(Object... args) {
		logger.error(buildOutputMessage(args));
	}

	public void group() {
		groupLevel++;
	}

	public void groupEnd() {
		groupLevel--;
		groupLevel = (groupLevel < 0 ? 0 : groupLevel);
	}

	public void log(Object... args) {
		logger.info(buildOutputMessage(args));
	}

	public void time(String label) {
		timers.put(label, System.currentTimeMillis());
	}

	public void timeEnd(String label) {
		Long current = System.currentTimeMillis();
		Long start = timers.get(label);
		if (start != null) {
			if (groupLevel > 0) {
				System.out.print(CharBuffer.allocate(groupLevel).toString().replace('\0', '\t'));
			}
			logger.info("Timer '" + label + "': " + (current - start) + "ms");
			timers.remove(label);
		}
	}

	public void warn(Object... args) {
		logger.warn(buildOutputMessage(args));
	}

}
