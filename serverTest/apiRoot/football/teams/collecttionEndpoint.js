'use strict';

(function() {
	var config = require('./config').config;
	var endpointUtils = require('../../../utils/endpointUtils').utils;
	registerMapping(config.uri, {
		'GET': doGet,
		'DELETE': doDelete,
		'POST': doPost,
		'OPTIONS': doOptions
	});

	function doGet(request, response) {
		var cursor = config.collection.find(mongo.createBasicDBObject(), config.dbFields);
		// build any sorting from query 'sort' params...
		var sortObj = endpointUtils.getDBSortObjectFromQueryParams(request.getQueryParameter("sort"),  config.collectionPropertyOptions);
		if (sortObj !== null) {
			cursor.sort(sortObj);
		}
		var result = [];
		while (cursor.hasNext()) {
			result.push(config.refObject(cursor.next()));
		}
		cursor.close();
		return result;
	}

	function doDelete(request, response) {
		var dbobj = mongo.createBasicDBObject();
		config.collection.remove(dbobj);
		response.setStatus(204);
		return response;
	}

	function doPost(request, response) {
		var body = request.getBodyString();
		var bodyJson;
		try {
			bodyJson = JSON.parse(body);
		} catch (e) {
			throw new com.adeptions.exceptions.BadRequestException("Invalid JSON request (" + e + ")");
		}
		// TODO - check posted properties
		// TODO - add to collection
		// TODO - return new entity
	}

	function doOptions(request, response) {
		return {
			'properties': config.collectionPropertyOptions
		}
	}

})();
