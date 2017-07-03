'use strict';

exports.exceptions = {
	'BadRequestException': Java.type('com.adeptions.exceptions.BadRequestException'),
	'ConflictException': Java.type('com.adeptions.exceptions.ConflictException'),
	'ExpectationFailedException': Java.type('com.adeptions.exceptions.ExpectationFailedException'),
	'ForbiddenException': Java.type('com.adeptions.exceptions.ForbiddenException'),
	'GoneException': Java.type('com.adeptions.exceptions.GoneException'),
	'ImaTeapotException': Java.type('com.adeptions.exceptions.ImaTeapotException'),
	'NotAcceptableException': Java.type('com.adeptions.exceptions.NotAcceptableException'),
	'NotFoundException': Java.type('com.adeptions.exceptions.NotFoundException'),
	'PreconditionFailedException': Java.type('com.adeptions.exceptions.PreconditionFailedException'),
	'PreconditionRequiredException': Java.type('com.adeptions.exceptions.PreconditionRequiredException'),
	'RequestedRangeNotSatisfiableException': Java.type('com.adeptions.exceptions.RequestedRangeNotSatisfiableException'),
	'UnauthotizedException': Java.type('com.adeptions.exceptions.UnauthotizedException'),
	400: Java.type('com.adeptions.exceptions.BadRequestException'),
	401: Java.type('com.adeptions.exceptions.UnauthotizedException'),
	403: Java.type('com.adeptions.exceptions.ForbiddenException'),
	404: Java.type('com.adeptions.exceptions.NotFoundException'),
	406: Java.type('com.adeptions.exceptions.NotAcceptableException'),
	409: Java.type('com.adeptions.exceptions.ConflictException'),
	410: Java.type('com.adeptions.exceptions.GoneException'),
	412: Java.type('com.adeptions.exceptions.PreconditionFailedException'),
	416: Java.type('com.adeptions.exceptions.RequestedRangeNotSatisfiableException'),
	417: Java.type('com.adeptions.exceptions.ExpectationFailedException'),
	418: Java.type('com.adeptions.exceptions.ImaTeapotException'),
	428: Java.type('com.adeptions.exceptions.PreconditionRequiredException')
};

