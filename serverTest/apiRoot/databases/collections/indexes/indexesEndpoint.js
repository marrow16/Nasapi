'use strict';

(function() {
	var uri = '/databases';
	var uriSub = '/collections';
	var uriSubSub = '/indexes';
	var exceptions = require('../../../../utils/exceptions').exceptions;

	registerMapping(uri + '/{databaseName: [a-zA-Z0-9\\-\\.\\_]+}' + uriSub + '/{collectionName: [a-zA-Z0-9\\-\\.\\_]+}' + uriSubSub, {
		'GET': doGet,
		'POST': doPost,
		'DELETE': doDelete
	});

	function refObject(databaseName, collectionName, dbobj) {
		dbobj['$ref'] = uri + '/' + databaseName + uriSub + '/' + collectionName + uriSubSub + '/' + dbobj['name'];
		return dbobj;
	}

	function doGet(request, response) {
		var databaseName = request.getPathParameterFirst('databaseName');
		var collectionName = request.getPathParameterFirst('collectionName');
		var db = mongo.getDatabase(databaseName);
		if (db !== null) {
			var collection = db.getCollection(collectionName);
			if (collection !== null) {
				var indexes = collection.getIndexInfo();
				var result = [];
				for (var i = 0, imax = indexes.length; i < imax; i++) {
					result.push(refObject(databaseName, collectionName, indexes[i]));
				}
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
		if (!bodyJson.hasOwnProperty('keys')) {
			throw new exceptions.BadRequestException("Create index must specify 'keys' property");
		}
		var keys = bodyJson['keys'];
		var options = bodyJson['options'];
		var databaseName = request.getPathParameterFirst('databaseName');
		var collectionName = request.getPathParameterFirst('collectionName');
		var db = mongo.getDatabase(databaseName);
		if (db !== null) {
			var collection = db.getCollection(collectionName);
			if (collection !== null) {
				if (options) {
					collection.createIndex(mongo.createBasicDBObject(keys), mongo.createBasicDBObject(options));
				} else {
					collection.createIndex(mongo.createBasicDBObject(keys));
				}
				response.setStatus(201);
				return null;
			} else {
				throw new exceptions.NotFoundException("Collection '" + collectionName + "' does not exist in database '" + databaseName + "'");
			}
		} else {
			throw new exceptions.NotFoundException("Database '" + databaseName + "' does not exist");
		}
	}

	function doDelete(request, response) {
		var databaseName = request.getPathParameterFirst('databaseName');
		var collectionName = request.getPathParameterFirst('collectionName');
		var db = mongo.getDatabase(databaseName);
		if (db !== null) {
			var collection = db.getCollection(collectionName);
			if (collection !== null) {
				collection.dropIndexes();
				response.setStatus(204);
				return null;
			} else {
				throw new exceptions.NotFoundException("Collection '" + collectionName + "' does not exist in database '" + databaseName + "'");
			}
		} else {
			throw new exceptions.NotFoundException("Database '" + databaseName + "' does not exist");
		}
	}
})();
