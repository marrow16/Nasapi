'use strict';

exports.authenticator = new Authenticator();

function Authenticator() {
	var collectionName = 'users';
	var collection = mongo.getCollection(collectionName);
	var adminUsername = 'admin';
	// initial check to see if there are any users stored...
	if (Number(collection.count()) === 0) {
		// no users - so create an admin user...
		var adminPassword = java.util.UUID.randomUUID().toString();
		var dbobj = mongo.createBasicDBObject({
			'username': adminUsername,
			'password': adminPassword,
			'roles': ['USER', 'ADMIN_USER']
		});
		collection.save(dbobj);
		console.log("AUTHENTICATION CREATED ADMIN USER: Username 'admin', password: '" + adminPassword + "'");
	} else {
		// in case we've forgotten the admin user password - we're going to console log it...
		var adminFound = collection.findOne(mongo.createBasicDBObject('username', adminUsername));
		if (adminFound) {
			console.log('ADMIN USER PASSWORD:- ', adminFound['password']);
		}
	}
	// register the authenticator function...
	registerAuthenticator(authenticate);

	function authenticate(username, authenticationResponse) {
		var found = collection.findOne(mongo.createBasicDBObject('username', username));
		if (found) {
			authenticationResponse.setPassword(found['password']);
			authenticationResponse.getRoles().addAll(found['roles']);
			// return the authenticator response (containing the info to authenticate - or not)...
			return authenticationResponse;
		}
		// not found user - return null will cause auth fail...
		return null;
	}
}
