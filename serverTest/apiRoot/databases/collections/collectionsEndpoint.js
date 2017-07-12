'use strict';

(function() {
	var uri = '/databases';
	var uriSub = '/collections';
	var exceptions = require('../../../utils/exceptions').exceptions;

	registerMapping(uri + '/{databaseName: [a-zA-Z0-9\\-\\_]+}' + uriSub, {
		'GET': doGet
	});

	function doGet(request, response) {
		var databaseName = request.getPathParameterFirst('databaseName');
		var db = mongo.getDatabase(databaseName);
		if (db !== null) {
			var collectionNamesIterator = db.getCollectionNames().iterator();
			var result = [];
			while (collectionNamesIterator.hasNext()) {
				var collectionName = collectionNamesIterator.next();
				result.push({
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
				});
			}
			return result;
		} else {
			throw new exceptions.NotFoundException("Database '" + databaseName + "' does not exist");
		}
	}

})();

