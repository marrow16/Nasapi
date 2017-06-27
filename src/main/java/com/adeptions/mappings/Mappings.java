package com.adeptions.mappings;

import com.adeptions.exceptions.MappingException;
import com.adeptions.exceptions.NasapiException;
import com.adeptions.mappings.context.Console;
import com.adeptions.mappings.functions.RegisterMappingFunction;
import com.adeptions.mongo.MongoContainer;
import com.coveo.nashorn_modules.FilesystemFolder;
import com.coveo.nashorn_modules.Require;
import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Mappings implements RegisterMappingFunction {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private MappingTree mappingTree = new MappingTree();
	private NashornScriptEngine engine;
	private Bindings bindings;
	private File mainFile;
	private File mainDir;
	private String[] startupArgs;
	private MongoContainer mongoContainer;

	public Mappings(String[] startupArgs, MongoContainer mongoContainer) throws IOException, ScriptException, NasapiException {
		this.startupArgs = startupArgs;
		this.mongoContainer = mongoContainer;
		logger.info("Starting Nasapi");
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
		init();
	}

	private void init() throws ScriptException, IOException {
		logger.info("Initialising endpoint mappings");
		mappingTree = new MappingTree();
		logger.info("Starting Nashorn Script Engine");
		// ok, let's create our Nashorn engine...
		engine = (NashornScriptEngine)new NashornScriptEngineFactory().getScriptEngine();
		// enable CommonJS require...
		logger.info("Enabling CommonJS require");
		FilesystemFolder requiresFolder = FilesystemFolder.create(mainDir, "UTF-8");
		Require.enable(engine, requiresFolder);
		// make console available...
		bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("console", new Console());
		// make the registerMapping function available to scripts...
		bindings.put("registerMapping", this);
		// and pass in the startup arguments (in case the scripts need something from them)...
		bindings.put("arguments", startupArgs);
		// make the mongo container acessible from scripts...
		bindings.put("mongo", mongoContainer);
		// make the reload function available from scripts...
		bindings.put("reload", new ReloadFunctionImpl(this));
		// make the bind function available from scripts...
		bindings.put("bind", new BindFunctionImpl(this));
		// and run the main script file...
		String loadMainPath = mainFile.getCanonicalPath().replace('\\', '/');
		logger.info("Running " + mainFile.getName());
		engine.eval("load('" + loadMainPath + "');");
	}

	public Mapping find(String path, @NotNull MultivaluedMap<String,String> pathParametersFound) {
		return mappingTree.find(path, pathParametersFound);
	}

	public Mapping find(String path) {
		return mappingTree.find(path);
	}

	@Override
	public Mapping registerMapping(String path, Map<String,Object> methods) throws ScriptException, NashornException, NoSuchMethodException, MappingException {
		logger.info("registerMapping - path '" + path + "'");
		Mapping result = new Mapping(path, methods);
		mappingTree.put(result);
		return result;
	}

	void reload() throws ScriptException, IOException {
		logger.info("Re-starting Nasapi");
		// just call init to reload everything...
		init();
	}

	void bind(String name, Object object) {
		bindings.put(name, object);
	}
}
