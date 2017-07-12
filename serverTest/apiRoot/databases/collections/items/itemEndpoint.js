'use strict';

(function() {
	var uri = '/databases';
	var uriSub = '/collections';
	var uriSubSub = '/items';
	var exceptions = require('../../../../utils/exceptions').exceptions;
	var utils = require('../../utils').utils;

	registerMapping(uri + '/{databaseName: [a-zA-Z0-9\\-\\.\\_]+}' + uriSub + '/{collectionName: [a-zA-Z0-9\\-\\.\\_]+}' + uriSubSub + '/{itemId: [a-zA-Z0-9\\-\\.\\_]+}', {
		'GET': doGet,
		'DELETE': doDelete,
		'PUT': doPut,
		'PATCH': doPatch
	});

	function refObject(databaseName, collectionName, dbobj) {
		dbobj['$ref'] = uri + '/' + databaseName + uriSub + '/' + collectionName + uriSubSub + '/' + mongo.getObjectIdString(dbobj);
		return dbobj;
	}

	function doGet(request, response) {
		var databaseName = request.getPathParameterFirst('databaseName');
		var collectionName = request.getPathParameterFirst('collectionName');
		var itemId = request.getPathParameterFirst('itemId');
		var db = mongo.getDatabase(databaseName);
		if (db !== null) {
			var collection = db.getCollection(collectionName);
			if (collection !== null) {
				var idObject = mongo.createObjectId(itemId);
				if (idObject === null) {
					idObject = itemId;
//					throw new exceptions.NotFoundException("Cannot find item with invalid id '" + itemId + "' in collection '" + collectionName + "' in database '" + databaseName + "'");
				}
				var query = mongo.createBasicDBObject('_id', idObject);
				var found = collection.findOne(query);
				if (found !== null) {
					return refObject(databaseName, collectionName, found);
				} else {
					throw new exceptions.NotFoundException("Cannot find item with id '" + itemId + "' in collection '" + collectionName + "' in database '" + databaseName + "'");
				}
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
		var itemId = request.getPathParameterFirst('itemId');
		var db = mongo.getDatabase(databaseName);
		if (db !== null) {
			var collection = db.getCollection(collectionName);
			if (collection !== null) {
				var idObject = mongo.createObjectId(itemId);
				if (idObject === null) {
					throw new exceptions.NotFoundException("Cannot find item with invalid id '" + itemId + "' in collection '" + collectionName + "' in database '" + databaseName + "'");
				}
				var query = mongo.createBasicDBObject('_id', idObject);
				var found = collection.findOne(query);
				if (found !== null) {
					collection.remove(found);
					response.setStatus(204);
					return response;
				} else {
					throw new exceptions.NotFoundException("Cannot find item with id '" + itemId + "' in collection '" + collectionName + "' in database '" + databaseName + "'");
				}
			} else {
				throw new exceptions.NotFoundException("Collection '" + collectionName + "' does not exist in database '" + databaseName + "'");
			}
		} else {
			throw new exceptions.NotFoundException("Database '" + databaseName + "' does not exist");
		}
	}

	function doPut(request, response) {
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
		var itemId = request.getPathParameterFirst('itemId');
		var db = mongo.getDatabase(databaseName);
		if (db !== null) {
			var collection = db.getCollection(collectionName);
			if (collection !== null) {
				var idObject = mongo.createObjectId(itemId);
				if (idObject === null) {
					throw new exceptions.NotFoundException("Cannot find item with invalid id '" + itemId + "' in collection '" + collectionName + "' in database '" + databaseName + "'");
				}
				var query = mongo.createBasicDBObject('_id', idObject);
				var found = collection.findOne(query);
				if (found !== null) {
					// replace it...
					var replacement = mongo.createBasicDBObject(bodyJson);
					replacement['_id'] = idObject;
					collection.save(replacement);
					return refObject(databaseName, collectionName, replacement);
				} else {
					throw new exceptions.NotFoundException("Cannot find item with id '" + itemId + "' in collection '" + collectionName + "' in database '" + databaseName + "'");
				}
			} else {
				throw new exceptions.NotFoundException("Collection '" + collectionName + "' does not exist in database '" + databaseName + "'");
			}
		} else {
			throw new exceptions.NotFoundException("Database '" + databaseName + "' does not exist");
		}
	}

	function doPatch(request, response) {
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
		var itemId = request.getPathParameterFirst('itemId');
		var db = mongo.getDatabase(databaseName);
		if (db !== null) {
			var collection = db.getCollection(collectionName);
			if (collection !== null) {
				var idObject = mongo.createObjectId(itemId);
				if (idObject === null) {
					throw new exceptions.NotFoundException("Cannot find item with invalid id '" + itemId + "' in collection '" + collectionName + "' in database '" + databaseName + "'");
				}
				var query = mongo.createBasicDBObject('_id', idObject);
				var found = collection.findOne(query);
				if (found !== null) {
					// create patch updater object...
					var updater = mongo.createBasicDBObject('$set', mongo.createBasicDBObject(bodyJson));
					// and update it...
					collection.update(found, updater);
					// return the updated...
					return refObject(databaseName, collectionName, collection.findOne(query));
				} else {
					throw new exceptions.NotFoundException("Cannot find item with id '" + itemId + "' in collection '" + collectionName + "' in database '" + databaseName + "'");
				}
			} else {
				throw new exceptions.NotFoundException("Collection '" + collectionName + "' does not exist in database '" + databaseName + "'");
			}
		} else {
			throw new exceptions.NotFoundException("Database '" + databaseName + "' does not exist");
		}
	}
})();

