'use strict';

var exceptions = require('./exceptions').exceptions;

exports.utils = {
	allowedComparators: {
		'$eq': true,
		'$ne': true,
		'$gt': true,
		'$gte': true,
		'$lt': true,
		'$lte': true,
		'$regex': true
	},
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
		return result;
	},
	getFieldsRequested: function(queryParams, collectionProperties) {
		var fieldsObj = mongo.createBasicDBObject();
		fieldsObj.append('_id', 1);
		var pty;
		// fill in the default listed properties...
		for (pty in collectionProperties) {
			if (collectionProperties.hasOwnProperty(pty) && collectionProperties[pty].defaultListed) {
				fieldsObj.append(pty, 1);
			}
		}
		if (queryParams !== null) {
			for (var s = 0, smax = queryParams.length; s < smax; s++) {
				var paramValue = queryParams[s];
				var names = [paramValue];
				if (paramValue.indexOf(',') !== -1) {
					names = paramValue.split(',');
				}
				for (var n = 0, nmax = names.length; n < nmax; n++) {
					var name = names[n];
					if (name === '*') {
						for (pty in collectionProperties) {
							if (collectionProperties.hasOwnProperty(pty)) {
								fieldsObj.append(pty, 1);
							}
						}
					} else if (name.substr(0,1) === '-' && collectionProperties.hasOwnProperty(name.substr(1))) {
						fieldsObj.removeField(name.substr(1));
					} else if (collectionProperties.hasOwnProperty(name)) {
						fieldsObj.append(name, 1);
					}
				}
			}
		}
		return fieldsObj;
	},
	checkPropertyType: function(propertyName, propertyInfo, value) {
		if (value === null && !propertyInfo.nullabe) {
			throw new exceptions.BadRequestException("Property '" + propertyName + "' cannot be null");
		}
		var foundType = (Array.isArray(value) ? 'array' : typeof value);
		if (foundType !== propertyInfo.type) {
			switch (propertyInfo.type) {
				case 'long':
				case 'integer':
					if (foundType === 'number' && Math.round(value) === value) {
						return;
					}
					break;
				case 'double':
					if (foundType === 'number') {
						return;
					}
					break;
			}
			throw new exceptions.BadRequestException("Property '" + propertyName + "' is incorrect type - expected '" + propertyInfo.type + "' but found '" + foundType + "'");
		}
	},
	getFilteringRequested: function(queryParams, collectionProperties) {
		var filterObj = mongo.createBasicDBObject();
		var squareAt,slashAt;
		var filters = [],filter,compare,valueType;
		var pty;
		for (pty in queryParams) {
			var propertyName = pty;
			var comparator = '$eq';
			var regexOptions = null;
			if ((squareAt = propertyName.indexOf('[')) !== -1 && propertyName.substr(propertyName.length - 1) === ']') {
				comparator = '$' + propertyName.substring(squareAt + 1, propertyName.length - 1);
				if (comparator.substr(0, '$regex/'.length) === '$regex/') {
					regexOptions = comparator.substr('$regex/'.length);
					comparator = '$regex';
				}
				propertyName = propertyName.substring(0, squareAt);
			}
			if (collectionProperties.hasOwnProperty(propertyName)) {
				if (!collectionProperties[propertyName].filterable) {
					throw new exceptions.BadRequestException("Filter cannot be applied to property '" + propertyName + "'");
				}
				valueType = collectionProperties[propertyName].type;
				if (!this.allowedComparators.hasOwnProperty(comparator)) {
					throw new exceptions.BadRequestException("Filter comparator '" + comparator.substr(1) + "' invalid (filter property '" + propertyName + "')");
				}
				var paramValues = queryParams[pty];
				for (var s = 0, smax = paramValues.length; s < smax; s++) {
					filter = {};
					compare = {};
					switch (valueType) {
						case 'integer':
						case 'long':
							compare[comparator] = parseInt(paramValues[s]);
							break;
						case 'number':
						case 'float':
						case 'double':
							compare[comparator] = parseFloat(paramValues[s]);
							break;
						default:
							compare[comparator] = paramValues[s];
					}
					if (comparator === '$regex' && regexOptions !== null) {
						compare['$options'] = regexOptions;
					}
					filter[propertyName] = compare;
					filters.push(filter);
				}
			}
		}
		if (filters.length > 0) {
			filterObj = mongo.createBasicDBObject({
				'$and': filters
			});
		}
		return filterObj;
	},
	checkPostObject: function (postBody, collectionProperties) {
		var insertObj = mongo.createBasicDBObject();
		var mapSeenProperties = {};
		var pty;
		// copy posted properties into db insert object (if they are postable)...
		for (pty in postBody) {
			if (postBody.hasOwnProperty(pty) &&
				collectionProperties.hasOwnProperty(pty) && collectionProperties[pty].postable) {
				this.checkPropertyType(pty, collectionProperties[pty], postBody[pty]);
				mapSeenProperties[pty] = true;
				insertObj[pty] = postBody[pty];
			}
		}
		// check that all the mandatory post properties were supplied...
		var mandatoryPropertiesMissing = [];
		for (pty in collectionProperties) {
			if (collectionProperties.hasOwnProperty(pty) && collectionProperties[pty].postable &&
				collectionProperties[pty].postMandatory && !postBody.hasOwnProperty(pty)) {
				mandatoryPropertiesMissing.push("'" + pty + "'");
			}
		}
		if (mandatoryPropertiesMissing.length > 0) {
			throw new exceptions.BadRequestException("Mandatory properties missing - " + mandatoryPropertiesMissing.join(","));
		}
		return insertObj;
	},
	checkPutObject: function(putBody, entityProperties) {
		var updateObj = mongo.createBasicDBObject();
		// copy posted properties into db insert object (if they are postable)...
		for (var pty in putBody) {
			if (putBody.hasOwnProperty(pty) &&
				entityProperties.hasOwnProperty(pty) && entityProperties[pty].putable) {
				this.checkPropertyType(pty, entityProperties[pty], putBody[pty]);
				updateObj[pty] = putBody[pty];
			}
		}
		return updateObj;
	},
	issueId: function() {
		return java.util.UUID.randomUUID().toString();
	},
	issueEtag: function() {
		return java.util.UUID.randomUUID().toString();
	},
	checkEtagIfMatch: function(ifMatchHeader, entityEtag) {
		if (ifMatchHeader !== null && ifMatchHeader !== '*' && entityEtag !== null) {
			// strip quotes off of if-match header...
			var matchEtag = ifMatchHeader;
			if (matchEtag.substr(0,1) === '"') {
				matchEtag = matchEtag.substr(1);
			}
			if (matchEtag.substr(matchEtag.length - 1) === '"') {
				matchEtag = matchEtag.substr(0, matchEtag.length - 1);
			}
			// check they are equal...
			if (entityEtag !== matchEtag) {
				throw new exceptions.PreconditionFailedException("If-Match ETag does not match current entity ETag")
			}
		}
	}
};
