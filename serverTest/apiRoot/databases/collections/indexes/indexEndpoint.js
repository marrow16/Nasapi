'use strict';

(function() {
	var uri = '/databases';
	var uriSub = '/collections';
	var uriSubSub = '/indexes';
	var exceptions = require('../../../../utils/exceptions').exceptions;

	registerMapping(uri + '/{databaseName: [a-zA-Z0-9\\-\\.\\_]+}' + uriSub + '/{collectionName: [a-zA-Z0-9\\-\\.\\_]+}' + uriSubSub + '/{indexName: [a-zA-Z0-9\\-\\.\\_]+}', {
		'GET': doGet,
		'DELETE': doDelete
	});

	function refObject(databaseName, collectionName, dbobj) {
		dbobj['$ref'] = uri + '/' + databaseName + uriSub + '/' + collectionName + uriSubSub + '/' + dbobj['name'];
		return dbobj;
	}

	function doGet(request, response) {
		var databaseName = request.getPathParameterFirst('databaseName');
		var collectionName = request.getPathParameterFirst('collectionName');
		var indexName = request.getPathParameterFirst('indexName');
		var db = mongo.getDatabase(databaseName);
		if (db !== null) {
			var collection = db.getCollection(collectionName);
			if (collection !== null) {
				var indexes = collection.getIndexInfo();
				var result = null;
				for (var i = 0, imax = indexes.length; i < imax; i++) {
					if (indexes[i]['name'] === indexName) {
						result = refObject(databaseName, collectionName, indexes[i]);
						break;
					}
				}
				if (result) {
					return result;
				} else {
					throw new exceptions.NotFoundException("Index '" + indexName + "' does not exist (on collection '" + collectionName + "', database '" + databaseName + "')");
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
		var indexName = request.getPathParameterFirst('indexName');
		var db = mongo.getDatabase(databaseName);
		if (db !== null) {
			var collection = db.getCollection(collectionName);
			if (collection !== null) {
				var indexes = collection.getIndexInfo();
				var result = null;
				for (var i = 0, imax = indexes.length; i < imax; i++) {
					if (indexes[i]['name'] === indexName) {
						result = refObject(databaseName, collectionName, indexes[i]);
						break;
					}
				}
				if (result) {
					collection.dropIndex(indexName);
					response.setStatus(204);
					return null;
				} else {
					throw new exceptions.NotFoundException("Index '" + indexName + "' does not exist (ollection '" + collectionName + "', database '" + databaseName + "')");
				}
			} else {
				throw new exceptions.NotFoundException("Collection '" + collectionName + "' does not exist in database '" + databaseName + "'");
			}
		} else {
			throw new exceptions.NotFoundException("Database '" + databaseName + "' does not exist");
		}
	}
})();

