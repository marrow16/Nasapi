'use strict';

(function() {
	var uri = '/databases';
	var exceptions = require('../../utils/exceptions').exceptions;

	registerMapping(uri + '/{databaseName: [a-zA-Z0-9\\-\\.\\_]+}', {
		'GET': doGet
	});

	function doGet(request, response) {
		var databaseName = request.getPathParameterFirst('databaseName');
		var db = mongo.getDatabase(databaseName);
		if (db !== null) {
			return {
				'name': databaseName,
				'$ref': uri + '/' + databaseName,
				'collections': {
					'$ref': uri + '/' + databaseName + '/collections'
				}
			};
		} else {
			throw new exceptions.NotFoundException("Database '" + databaseName + "' does not exist");
		}
	}

})();


