'use strict';

var exceptions = require('../../utils/exceptions').exceptions;

exports.utils = {
	stripUnwantedProperties: function(obj) {
		if (obj !== null) {
			if (typeof obj === 'object') {
				if (Array.isArray(obj)) {
					for (var i = 0, imax = obj.length; i < imax; i++) {
						this.stripUnwantedProperties(obj[i]);
					}
				} else {
					for (var pty in obj) {
						if (obj.hasOwnProperty(pty)) {
							if (pty === '_id' || pty.substr(0,1) === '$') {
								delete obj[pty];
							} else {
								this.stripUnwantedProperties(obj[pty]);
							}
						}
					}
				}
			}
		}
	},
	getRequestedRange: function(request) {
		function getAfterToken(part, tokenLen) {
			var result = part.substr(tokenLen).trim();
			return (result.substr(0,1) === '=' ? result.substr(1).trim() : result);
		}
		var result = {};
		var rangeHeader = request.getHeader('Range');
		if (rangeHeader !== null) {
			var rangeParts = rangeHeader.split(',');
			var rangePart,afterToken,intValue,hyphenParts;
			for (var i = 0, imax = rangeParts.length; i < imax; i++) {
				rangePart = rangeParts[i].trim();
				if (rangePart.substr(0,'start'.length) === 'start') {
					afterToken = getAfterToken(rangePart, 'start'.length);
					intValue = parseInt(afterToken);
					if (intValue === intValue && intValue >= 0) {
						result['start'] = intValue;
					} else {
						throw new exceptions.RequestedRangeNotSatisfiableException("Range 'start' must be numeric greater than or equal to zero");
					}
				} else if (rangePart.substr(0,'items'.length) === 'items') {
					afterToken = getAfterToken(rangePart, 'items'.length);
					hyphenParts = afterToken.split('-');
					if (hyphenParts.length === 2) {
						intValue = parseInt(hyphenParts[0]);
						if (intValue === intValue && intValue >= 0) {
							result['start'] = intValue;
							intValue = parseInt(hyphenParts[1]);
							if (intValue === intValue && intValue >= result['start']) {
								result['count'] = (intValue - result['start']) + 1;
							} else {
								throw new exceptions.RequestedRangeNotSatisfiableException("Range 'items' end part must be numeric greater than or equal to start part");
							}
						} else {
							throw new exceptions.RequestedRangeNotSatisfiableException("Range 'items' start part must be numeric greater than or equal to zero");
						}
					} else {
						throw new exceptions.RequestedRangeNotSatisfiableException("Range 'items' must contain start and end separated by '-'");
					}
				} else if (rangePart.substr(0,'count'.length) === 'count') {
					afterToken = getAfterToken(rangePart, 'count'.length);
					intValue = parseInt(afterToken);
					if (intValue === intValue && intValue > 0) {
						result['count'] = intValue;
					} else {
						throw new exceptions.RequestedRangeNotSatisfiableException("Range 'count' must be numeric greater than zero");
					}
				}
			}
		}
		return result;
	},
	getRequestedSort: function(request) {
		var result = null;
		var sortParamValues = request.getQueryParameter("$sort");
		if (sortParamValues !== null) {
			result = mongo.createBasicDBObject();
			for (var s = 0, smax = sortParamValues.length; s < smax; s++) {
				var sortField = sortParamValues[s];
				var ascending = true;
				if (sortField.substr(0,1) === '-') {
					ascending = false;
					sortField = sortField.substr(1);
				}
				result.append(sortField, (ascending ? 1 : -1));
			}
		}
		return result;
	},
	getFiltering: function(request) {
		var result = null;
		var filterParamValue = request.getQueryParameterFirst("$filter");
		if (filterParamValue !== null) {
			result = mongo.parseDBObject(filterParamValue);
		}
		return result;
	}
};

