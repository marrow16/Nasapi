'use strict';

(function() {
	var uri = '/databases';
	var uriSub = '/collections';
	var exceptions = require('../../../utils/exceptions').exceptions;

	registerMapping(uri + '/{databaseName: [a-zA-Z0-9\\-\\.]+}' + uriSub + '/{collectionName: [a-zA-Z0-9\\-\\.]+}', {
		'GET': doGet
	});

	function doGet(request, response) {
		var databaseName = request.getPathParameterFirst('databaseName');
		var collectionName = request.getPathParameterFirst('collectionName');
		var db = mongo.getDatabase(databaseName);
		if (db !== null) {
			var collection = db.getCollection(collectionName);
			if (collection !== null) {
				return {
					'name': collectionName,
					'$ref': uri + '/' + databaseName + uriSub + '/' + collectionName,
					'count': db.getCollection(collectionName).count(),
					'items': {
						'$ref': uri + '/' + databaseName + uriSub + '/' + collectionName + '/items'
					},
					'database': {
						'name': databaseName,
						'$ref': uri + '/' + databaseName
					}
				}
			} else {
				throw new exceptions.NotFoundException("Collection '" + collectionName + "' does not exist in database '" + databaseName + "'");
			}
		} else {
			throw new exceptions.NotFoundException("Database '" + databaseName + "' does not exist");
		}
	}

})();

