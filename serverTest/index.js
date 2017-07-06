'use strict';

// register authenticator...
require('./authentication/authenticator');

// require the endpoints...
require('./apiRoot/football/teams/index');

// require the database api endpoints...
require('./apiRoot/databases/index');

// register a special endpoint so we can reload our scripts, during development, without having to restart the server...
registerMapping('/reload', {
	'POST': function(request, response) {
		reload();
		response.setStatus(202);
		return response;
	}
});