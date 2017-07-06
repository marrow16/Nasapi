'use strict';

(function() {
	var uri = '/databases';
	registerMapping(uri, {
		'GET': doGet
	});

	function doGet(request, response) {
		var databaseNames = mongo.getClient().getDatabaseNames();
		var result = [];
		for (var i = 0, imax = databaseNames.length; i < imax; i++) {
			result.push({
				'name': databaseNames[i],
				'$ref': uri + '/' + databaseNames[i]
			});
		}
		return result;
	}

})();

