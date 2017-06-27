'use strict';

exports.utils = {
	getDBSortObjectFromQueryParams: function(queryParams, collectionProperties) {
		var result = null;
		if (queryParams !== null) {
			var sortObj = mongo.createBasicDBObject();
			var anyAdded = false;
			for (var s = 0, smax = queryParams.length; s < smax; s++) {
				var sortField = queryParams[s];
				var ascending = true;
				if (sortField.substr(0,1) === '-') {
					ascending = false;
					sortField = sortField.substr(1);
				}
				if (collectionProperties.hasOwnProperty(sortField) && collectionProperties[sortField].sortable) {
					anyAdded = true;
					sortObj.append(sortField, (ascending ? 1 : -1));
				}
			}
			if (anyAdded) {
				result = sortObj;
			}
		}
		return sortObj;
	}
};
