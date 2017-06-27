package com.adeptions;

import com.adeptions.jersey.JerseyConfig;
import com.adeptions.exceptions.NasapiException;
import com.adeptions.mappings.Mappings;
import com.adeptions.mongo.MongoContainer;
import com.mongodb.MongoClient;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import javax.script.ScriptException;
import java.io.IOException;

@ComponentScan
@EnableAutoConfiguration
public class Nasapi {
	private static String[] startupArgs;
	private MongoDbFactory mongoDbFactory;
	private MongoMappingContext context;

	public Nasapi() {
	}

	/**
	 * Get the environment (to enable access to a application.properties)
	 */
	@Autowired
	private Environment env;

	@Bean
	public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory,
									   MongoMappingContext context) {
		MappingMongoConverter converter =
				new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), context);
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, converter);
		return mongoTemplate;
	}

	/**
	 * Create the Mongo container (used by scripts to access MongoDB)
	 * @param mongoClient - the MongoClient (from Spring)
	 * @param environment - the environment
	 * @return the MongoContainer
	 */
	@Bean
	private MongoContainer mongoContainer(MongoClient mongoClient,
										  MongoTemplate template,
										  MongoDbFactory mongoDbFactory,
										  MongoMappingContext context,
										  Environment environment) {
		return new MongoContainer(mongoClient, template, mongoDbFactory, context, environment);
	}

	/**
	 * Create the Mappings
	 * @param mongoContainer
	 * @return
	 * @throws IOException
	 * @throws ScriptException
	 */
	@Bean(name = "Mappings")
	private Mappings mappings(MongoContainer mongoContainer) throws IOException, ScriptException, NasapiException {
		return new Mappings(startupArgs, mongoContainer);
	}

	@Bean
	@DependsOn("Mappings")
	private ResourceConfig jerseyConfig() {
		return new JerseyConfig();
	}

	/**
	 * Main startup
	 *
	 * @param args the command line arguments
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		Nasapi.startupArgs = args;
		// start Spring application...
		new SpringApplicationBuilder()
				.sources(Nasapi.class)
				.run(args);
	}
}
