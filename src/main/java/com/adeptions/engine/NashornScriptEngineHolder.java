package com.adeptions.engine;

import com.adeptions.engine.globals.Console;
import com.adeptions.exceptions.NasapiException;
import com.adeptions.mappings.Mappings;
import com.adeptions.mongo.MongoContainer;
import com.coveo.nashorn_modules.FilesystemFolder;
import com.coveo.nashorn_modules.Require;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;

public class NashornScriptEngineHolder {
	private static final String FUNCTION_NAME_RELOAD = "reload";
	private static final String FUNCTION_NAME_BIND = "bind";
	private static final String BINDING_NAME_ARGUMENTS = "arguments";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private NashornScriptEngine engine;
	private Bindings bindings;
	private File mainFile;
	private File mainDir;
	private String[] startupArgs;
	private Mappings mappings;
	private MongoContainer mongoContainer;

	public NashornScriptEngineHolder(String[] startupArgs, Mappings mappings, MongoContainer mongoContainer) throws NasapiException, IOException, ScriptException {
		this.startupArgs = startupArgs;
		this.mappings = mappings;
		this.mongoContainer = mongoContainer;
		logger.info("Starting Nasapi");
		processStartupArgs();
		init();
	}

	private void processStartupArgs() throws NasapiException, IOException {
		logger.info("Starting Nasapi - Processing Startup Args");
		if (startupArgs.length > 0) {
			mainDir = new File(startupArgs[0]);
			if (!mainDir.exists()) {
				throw new NasapiException("Startup file/path '" + startupArgs[0] + "' does not exist!");
			}
			// was it a file or path specified...
			if (mainDir.isFile()) {
				// it's a file (use it as the alternate index.js)...
				mainFile = mainDir;
				mainDir = mainFile.getParentFile();
			} else {
				// it's a directory...
				mainFile = new File(mainDir.getCanonicalPath() + File.separator + "index.js");
			}
		} else {
			mainDir = new File(".");
			mainFile = new File(mainDir.getCanonicalPath() + File.separator + "index.js");
		}
		// just check that the main.js exists and isn't a directory...
		if (!mainFile.exists()) {
			throw new NasapiException("Startup path '" + mainDir.getCanonicalPath() + "' does not contain a 'index.js' file!");
		} else if (!mainFile.isFile()) {
			throw new NasapiException("Startup path '" + mainDir.getCanonicalPath() + "' contains a 'index.js' that is not a file!");
		}
	}

	private void init() throws ScriptException, IOException {
		logger.info("Starting Nashorn Script Engine");
		// ok, let's create our Nashorn engine...
		engine = (NashornScriptEngine)new NashornScriptEngineFactory().getScriptEngine();
		// enable CommonJS require...
		logger.info("Enabling CommonJS require");
		FilesystemFolder requiresFolder = FilesystemFolder.create(mainDir, "UTF-8");
		Require.enable(engine, requiresFolder);
		// make console available...
		bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put(Console.BINDING_NAME, new Console());
		// make the registerMapping function available to scripts...
		bindings.put(Mappings.FUNCTION_NAME_REGISTER_MAPPING, mappings);
		// and pass in the startup arguments (in case the scripts need something from them)...
		bindings.put(BINDING_NAME_ARGUMENTS, startupArgs);
		// make the mongo container acessible from scripts...
		bindings.put(MongoContainer.BINDING_NAME, mongoContainer);
		// make the reload function available from scripts...
		bindings.put(FUNCTION_NAME_RELOAD, new ReloadFunctionImpl(this));
		// make the bind function available from scripts...
		bindings.put(FUNCTION_NAME_BIND, new BindFunctionImpl(this));
		// and run the main script file...
		String loadMainPath = mainFile.getCanonicalPath().replace('\\', '/');
		logger.info("Running " + mainFile.getName());
		engine.eval("load('" + loadMainPath + "');");
	}

	void reload() throws ScriptException, IOException {
		logger.info("Reloading Nasapi");
		// clear the mappings...
		mappings.clear();
		// just call init to reload everything...
		init();
	}

	void bind(String name, Object object) {
		bindings.put(name, object);
	}

}
