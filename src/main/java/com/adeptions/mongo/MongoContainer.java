package com.adeptions.mongo;

import com.mongodb.*;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Map;

public class MongoContainer {
	private MongoClient mongoClient;
	private MongoTemplate template;
	private MongoDbFactory mongoDbFactory;
	private MongoMappingContext context;
	private String databaseName;
	private DB database;

	public MongoContainer(MongoClient mongoClient,
						  MongoTemplate template,
						  MongoDbFactory mongoDbFactory,
						  MongoMappingContext context,
						  Environment environment) {
		this.mongoClient = mongoClient;
		this.template = template;
		this.mongoDbFactory = mongoDbFactory;
		this.context = context;
//		databaseName = environment.getProperty("spring.data.mongodb.database");
//		if (databaseName == null) {
//			databaseName = "nasapi";
//		}
//		database = mongoClient.getDB(databaseName);
//		template.getDb();
	}

	public DBCollection getCollection(String name) {
		return template.getCollection(name);
	}

	public MongoTemplate getTemplate() {
		return template;
	}

	public DB getDatabase() {
		return template.getDb();
	}

	public DB getDatabase(String name) {
		return mongoClient.getDB(name);
	}

	public BasicDBObject createBasicDBObject() {
		return new BasicDBObject();
	}

	public BasicDBObject createBasicDBObject(String key, Object value) {
		return new BasicDBObject(key, value);
	}

	public BasicDBObject createBasicDBObject(Map map) {
		return new BasicDBObject(map);
	}
}
