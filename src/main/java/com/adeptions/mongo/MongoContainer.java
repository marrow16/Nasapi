package com.adeptions.mongo;

import com.adeptions.utils.ScriptObjectMirrors;
import com.mongodb.*;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import com.mongodb.util.JSON;

import java.util.Map;

public class MongoContainer {
	public static final String BINDING_NAME = "mongo";

	private MongoClient mongoClient;
	private MongoTemplate template;
	private MongoDbFactory mongoDbFactory;
	private MongoMappingContext context;

	public MongoContainer(MongoClient mongoClient,
						  MongoTemplate template,
						  MongoDbFactory mongoDbFactory,
						  MongoMappingContext context,
						  Environment environment) {
		this.mongoClient = mongoClient;
		this.template = template;
		this.mongoDbFactory = mongoDbFactory;
		this.context = context;
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
		if (map instanceof ScriptObjectMirror) {
			return new BasicDBObject((Map)ScriptObjectMirrors.convert(map));
		}
		return new BasicDBObject(map);
	}

	public DBObject parseDBObject(String json) {
		return (DBObject)JSON.parse(json);
	}
}
