'use strict';

(function() {
	var uri = '/databases';
	var uriSub = '/collections';
	var uriSubSub = '/items';
	var exceptions = require('../../../../utils/exceptions').exceptions;
	var utils = require('../../utils').utils;

	registerMapping(uri + '/{databaseName: [a-zA-Z0-9\\-\\.\\_]+}' + uriSub + '/{collectionName: [a-zA-Z0-9\\-\\.\\_]+}' + uriSubSub, {
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
				var filter = utils.getFiltering(request);
				var cursor = (filter !== null ? collection.find(filter) : collection.find());
				var totalCount = cursor.count();
				var sortObj = utils.getRequestedSort(request);
				if (sortObj !== null) {
					cursor.sort(sortObj);
				}
				var itemStart = 0;
				var itemEnd = totalCount - 1;
				var range = utils.getRequestedRange(request);
				if (range.hasOwnProperty('start') && range.start > 0) {
					if (range.start >= totalCount) {
						throw new exceptions.RequestedRangeNotSatisfiableException("Requested Range start is greater than total items");
					}
					itemStart = range.start;
					cursor.skip(range.start);
				}
				if (range.hasOwnProperty('count') && range.count > 0) {
					itemEnd = Math.max(totalCount - 1, range.count - itemStart);
					cursor.limit(range.count);
				}
				var result = [];
				while (cursor.hasNext()) {
					result.push(refObject(databaseName, collectionName, cursor.next()));
				}
				cursor.close();
				response.setHeader('Content-Range', 'items ' + itemStart + '-' + itemEnd + '/' + totalCount);
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

