'use strict';

(function() {
	var config = require('./config').config;
	var endpointUtils = require('../../../utils/endpointUtils').utils;
	var exceptions = require('../../../utils/exceptions').exceptions;
	registerMapping(config.uri, {
		'GET': doGet,
		'DELETE': doDelete,
		'POST': doPost,
		'OPTIONS': doOptions
	});

	function doGet(request, response) {
		var fields = endpointUtils.getFieldsRequested(request.getQueryParameter("properties"), config.collectionPropertyOptions);
		var finds = endpointUtils.getFilteringRequested(request.getQueryParameters(), config.collectionPropertyOptions);
		var cursor = config.collection.find(finds, fields);
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
		if (!request.hasRole('ADMIN_USER')) {
			throw new exceptions.ForbiddenException("Insufficient privileges");
		}
		var dbobj = mongo.createBasicDBObject();
		config.collection.remove(dbobj);
		response.setStatus(204);
		return response;
	}

	function doPost(request, response) {
		if (!request.hasRole('ADMIN_USER')) {
			throw new exceptions.ForbiddenException("Insufficient privileges");
		}
		var body = request.getBodyString();
		var bodyJson;
		try {
			bodyJson = JSON.parse(body);
		} catch (e) {
			throw new exceptions.BadRequestException("Invalid JSON request (" + e + ")");
		}
		var insertObj = endpointUtils.checkPostObject(bodyJson, config.collectionPropertyOptions);
		// check that the name doesn't conflict with any existing...
		var foundByName = config.collection.findOne(mongo.createBasicDBObject('name', bodyJson['name']));
		if (foundByName !== null) {
			throw new exceptions.ConflictException("Entity with name '" + bodyJson['name'] + "' already exists!");
		}
		// all ok, give it an etag...
		var etag = endpointUtils.issueEtag();
		insertObj['etag'] = etag;
		config.collection.insert(insertObj);
//		insertObj.removeField('_id');
		config.refObject(insertObj);
		response.setHeader('Etag', '"' + etag + '"');
		response.setStatus(201);
		return insertObj;
	}

	function doOptions(request, response) {
		return {
			'properties': config.collectionPropertyOptions
		}
	}

})();
