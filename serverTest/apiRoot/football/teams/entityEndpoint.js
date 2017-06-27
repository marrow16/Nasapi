'use strict';

(function() {
	var config = require('./config').config;
	registerMapping(config.uri + '/{id: [a-zA-Z0-9\\-]+}', {
		'GET': doGet,
		'DELETE': doDelete,
		'PUT': doPut,
		'OPTIONS': doOptions
	});

	function doGet(request, response) {
		var id = request.getPathParameterFirst('id');
		var found = config.collection.findOne(mongo.createBasicDBObject('id', id));
		if (found !== null) {
			return config.refObject(found);
		}
		throw new com.adeptions.exceptions.NotFoundException("Cannot find entity (id: '" + id + "')");
	}

	function doDelete(request, response) {
		var id = request.getPathParameterFirst('id');
		var found = config.collection.findOne(mongo.createBasicDBObject('id', id));
		if (found === null) {
			throw new com.adeptions.exceptions.NotFoundException("Cannot find entity (id: '" + id + "')");
		}
		config.collection.remove(found);
		response.setStatus(204);
		return response;
	}

	function doPut(request, response) {
		var body = request.getBodyString();
		var bodyJson;
		try {
			bodyJson = JSON.parse(body);
		} catch (e) {
			throw new com.adeptions.exceptions.BadRequestException("Invalid JSON request (" + e + ")");
		}
		// TODO - check put properties
		// TODO - find and update entity
		// TODO - return updated entity
	}

	function doOptions(request, response) {
		return {
			'properties': config.entityPropertyOptions
		}
	}

})();