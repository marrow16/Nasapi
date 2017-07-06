'use strict';

(function() {
	var uri = '/databases';
	var uriSub = '/collections';
	var uriSubSub = '/items';
	var exceptions = require('../../../../utils/exceptions').exceptions;
	var utils = require('../../utils').utils;

	registerMapping(uri + '/{databaseName: [a-zA-Z0-9\\-\\.]+}' + uriSub + '/{collectionName: [a-zA-Z0-9\\-\\.]+}' + uriSubSub, {
		'GET': doGet,
		'POST': doPost
	});

	function refObject(databaseName, collectionName, dbobj) {
		dbobj['$ref'] = uri + '/' + databaseName + uriSub + '/' + collectionName + uriSubSub + '/' + mongo.getObjectIdString(dbobj);
		return dbobj;
	}

	function doGet(request, response) {
		var databaseName = request.getPathParameterFirst('databaseName');
		var collectionName = request.getPathParameterFirst('collectionName');
		var db = mongo.getDatabase(databaseName);
		if (db !== null) {
			var collection = db.getCollection(collectionName);
			if (collection !== null) {
				var cursor = collection.find();
				var result = [];
				while (cursor.hasNext()) {
					result.push(refObject(databaseName, collectionName, cursor.next()));
				}
				cursor.close();
				return result;
			} else {
				throw new exceptions.NotFoundException("Collection '" + collectionName + "' does not exist in database '" + databaseName + "'");
			}
		} else {
			throw new exceptions.NotFoundException("Database '" + databaseName + "' does not exist");
		}
	}

	function doPost(request, response) {
		var body = request.getBodyString();
		var bodyJson;
		try {
			bodyJson = JSON.parse(body);
		} catch (e) {
			throw new exceptions.BadRequestException("Invalid JSON request (" + e + ")");
		}
		// strip any ids and $ref (or any $ prefixed) properties...
		utils.stripUnwantedProperties(bodyJson);
		var databaseName = request.getPathParameterFirst('databaseName');
		var collectionName = request.getPathParameterFirst('collectionName');
		var db = mongo.getDatabase(databaseName);
		if (db !== null) {
			var collection = db.getCollection(collectionName);
			if (collection !== null) {
				var dbobj = mongo.createBasicDBObject(bodyJson);
				collection.insert(dbobj);
				return refObject(databaseName, collectionName, dbobj);
			} else {
				throw new exceptions.NotFoundException("Collection '" + collectionName + "' does not exist in database '" + databaseName + "'");
			}
		} else {
			throw new exceptions.NotFoundException("Database '" + databaseName + "' does not exist");
		}
	}

})();

