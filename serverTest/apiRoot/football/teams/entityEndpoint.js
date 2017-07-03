'use strict';

(function() {
	var config = require('./config').config;
	var endpointUtils = require('../../../utils/endpointUtils').utils;
	var exceptions = require('../../../utils/exceptions').exceptions;
	registerMapping(config.uri + '/{id: [a-zA-Z0-9\\-]+}', {
		'GET': doGet,
		'DELETE': doDelete,
		'PUT': doPut,
		'OPTIONS': doOptions
	});

	function doGet(request, response) {
		var id = request.getPathParameterFirst('id');
		var found = config.collection.findOne(mongo.createBasicDBObject('id', id), config.dbFields);
		if (found !== null) {
			return config.refObject(found);
		}
		throw new exceptions.NotFoundException("Cannot find entity (id: '" + id + "')");
	}

	function doDelete(request, response) {
		if (!request.hasRole('ADMIN_USER')) {
			throw new exceptions.ForbiddenException("Insufficient privileges");
		}
		var id = request.getPathParameterFirst('id');
		var found = config.collection.findOne(mongo.createBasicDBObject('id', id));
		if (found === null) {
			throw new exceptions.NotFoundException("Cannot find entity (id: '" + id + "')");
		}
		// check etag if-match...
		endpointUtils.checkEtagIfMatch(request.getHeader('If-Match'), found['etag']);
		config.collection.remove(found);
		response.setStatus(204);
		return response;
	}

	function doPut(request, response) {
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
		var id = request.getPathParameterFirst('id');
		var found = config.collection.findOne(mongo.createBasicDBObject('id', id));
		if (found === null) {
			throw new exceptions.NotFoundException("Cannot find entity (id: '" + id + "')");
		}
		// check etag if-match...
		endpointUtils.checkEtagIfMatch(request.getHeader('If-Match'), found['etag']);
		response.setHeader('X-Pre-ETag', '"' + found['etag'] + '"');
		var updateObj = endpointUtils.checkPutObject(bodyJson, config.entityPropertyOptions);
		// if we're updating the name we need to check the new name doesn't conflict...
		var updatedName = updateObj['name'];
		if (updatedName !== null) {
			var findExistingQuery = mongo.createBasicDBObject('name', updatedName);
			findExistingQuery.append('id', mongo.createBasicDBObject('$ne', id));
			var findExisting = config.collection.findOne(findExistingQuery);
			if (findExisting !== null) {
				throw new exceptions.ConflictException("Entity with name '" + bodyJson['name'] + "' already exists!");
			}
		}
		// we are going to update the etag...
		var newEtag = endpointUtils.issueEtag();
		updateObj['etag'] = newEtag;
		response.setHeader('ETag', '"' + newEtag + '"');
		var updater = mongo.createBasicDBObject('$set', updateObj);
		config.collection.update(mongo.createBasicDBObject('id', id), updater);
		return config.refObject(config.collection.findOne(mongo.createBasicDBObject('id', id), config.dbFields));
	}

	function doOptions(request, response) {
		return {
			'properties': config.entityPropertyOptions
		}
	}

})();