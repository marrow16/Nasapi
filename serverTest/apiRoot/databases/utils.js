'use strict';

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
	}
};

