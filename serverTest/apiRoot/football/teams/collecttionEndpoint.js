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
		var authentication = request.getAuthentication();
		console.log('authentication', authentication);
		console.log('authentication.getName()', authentication.getName());
		console.log('authentication.getAuthorities()', authentication.getAuthorities());
		console.log('authentication.getCredentials()', authentication.getCredentials());
		console.log('authentication.getDetails()', authentication.getDetails());
		console.log('authentication.getPrincipal()', authentication.getPrincipal());
		console.log('authentication.isAuthenticated()', authentication.isAuthenticated());

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
		var insertObj = endpointUtils.checkPostObject(bodyJson, config.collectionPropertyOptions);
		// check that the name doesn't conflict with any existing...
		var foundByName = config.collection.findOne(mongo.createBasicDBObject('name', bodyJson['name']));
		if (foundByName !== null) {
			throw new com.adeptions.exceptions.ConflictException("Entity with name '" + bodyJson['name'] + "' already exists!");
		}
		// all ok, give it an id and etag...
		var id = endpointUtils.issueId();
		var etag = endpointUtils.issueEtag();
		insertObj['id'] = id;
		insertObj['etag'] = etag;
		config.collection.save(insertObj);
		insertObj.removeField('_id');
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
