'use strict';

exports.config = new EndpointConfig();

function EndpointConfig() {
	var uri = '/football/teams';
	// database collection stuff...
	var collectionName = 'teams';
	var collection = mongo.getCollection(collectionName);
	// initial check to see if there are any football teams populated in the mongo db collection...
	if (collection.count() == 0) {
		require('./initialTeamsLoader');
	}
	// build list of fields that we'll pull back at each query...
	var fields = {
		'id': 1,
		'name': 1,
		'city': 1,
		'owner': 1,
		'league': 1,
		'stadiumCapacity': 1,
		'formationYear': 1,
		'_id': 0
	};
	var baseProperties = {
		'id': {
			type: 'String',
			description: 'The id of the entity',
			$collection: {
				postable: false,
				postMandatory: false,
				sortable: true
			},
			$entity: {
				putable: false
			}
		},
		'$ref': {
			type: 'String',
			description: 'The URI of the entity',
			$collection: {
				postable: false,
				postMandatory: false,
				sortable: false
			},
			$entity: {
				putable: false
			}
		},
		'name': {
			type: 'String',
			description: 'The name of the team',
			$collection: {
				postable: true,
				postMandatory: true,
				sortable: true
			},
			$entity: {
				putable: true
			}
		},
		'city': {
			type: 'String',
			description: 'The city/town in which the team is based',
			$collection: {
				postable: true,
				postMandatory: true,
				sortable: true
			},
			$entity: {
				putable: true
			}
		},
		'owner': {
			type: 'String',
			description: 'The owner(s) of the team',
			$collection: {
				postable: true,
				postMandatory: false,
				sortable: true
			},
			$entity: {
				putable: true
			}
		},
		'league': {
			type: 'String',
			description: 'The league in which the team competes',
			$collection: {
				postable: true,
				postMandatory: false,
				sortable: true
			},
			$entity: {
				putable: true
			}
		},
		'stadiumCapacity': {
			type: 'Integer',
			description: 'The capacity of the team\'s stadium',
			$collection: {
				postable: true,
				postMandatory: false,
				sortable: true
			},
			$entity: {
				putable: true
			}
		},
		'formationYear': {
			type: 'Integer',
			description: 'The year tthe football club was formed',
			$collection: {
				postable: true,
				postMandatory: false,
				sortable: true
			},
			$entity: {
				putable: true
			}
		}
	};
	var collectionPropertyOptions = {};
	var entityPropertyOptions = {};
	for (var pty in baseProperties) {
		if (baseProperties.hasOwnProperty(pty)) {
			var propertyDef = baseProperties[pty];
			collectionPropertyOptions[pty] = {
				type: propertyDef.type,
				description: propertyDef.description,
				postable: propertyDef.$collection.postable,
				postMandatory: propertyDef.$collection.postMandatory,
				sortable: propertyDef.$collection.sortable
			};
			entityPropertyOptions[pty] = {
				type: propertyDef.type,
				description: propertyDef.description,
				putable: propertyDef.$entity.putable
			};
		}
	}

	return {
		collectionName: collectionName,
		collection: collection,
		fields: fields,
		dbFields: mongo.createBasicDBObject(fields),
		baseProperties: baseProperties,
		collectionPropertyOptions: collectionPropertyOptions,
		entityPropertyOptions: entityPropertyOptions,
		uri: uri,
		refObject: function(dbobj) {
			dbobj['$ref'] = uri + '/' + dbobj.id;
			return dbobj;
		}
	};
}