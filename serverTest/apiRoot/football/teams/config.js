'use strict';

exports.config = new EndpointConfig();

function EndpointConfig() {
	var uri = '/football/teams';
	// database collection stuff...
	var collectionName = 'teams';
	var collection = mongo.getCollection(collectionName);
	// initial check to see if there are any football teams populated in the mongo db collection...
	if (Number(collection.count()) === 0) {
		require('./initialTeamsLoader');
	}
	var baseProperties = {
		'id': {
			type: 'string',
			nullabe: false,
			description: 'The id of the entity',
			$collection: {
				postable: false,
				postMandatory: false,
				filterable: false,
				sortable: true,
				defaultListed: true
			},
			$entity: {
				putable: false
			}
		},
		'$ref': {
			type: 'string',
			nullabe: false,
			description: 'The URI of the entity',
			$collection: {
				postable: false,
				postMandatory: false,
				filterable: false,
				sortable: false,
				defaultListed: true
			},
			$entity: {
				putable: false
			}
		},
		'etag': {
			type: 'string',
			nullabe: false,
			description: 'The concurrency ETag of the entity',
			$collection: {
				postable: false,
				postMandatory: false,
				filterable: true,
				sortable: false,
				defaultListed: false
			},
			$entity: {
				putable: false
			}
		},
		'name': {
			type: 'string',
			nullabe: false,
			description: 'The name of the team',
			$collection: {
				postable: true,
				postMandatory: true,
				filterable: true,
				sortable: true,
				defaultListed: true
			},
			$entity: {
				putable: true
			}
		},
		'city': {
			type: 'string',
			nullabe: true,
			description: 'The city/town in which the team is based',
			$collection: {
				postable: true,
				postMandatory: true,
				filterable: true,
				sortable: true,
				defaultListed: false
			},
			$entity: {
				putable: true
			}
		},
		'owner': {
			type: 'string',
			nullabe: true,
			description: 'The owner(s) of the team',
			$collection: {
				postable: true,
				postMandatory: false,
				filterable: true,
				sortable: true,
				defaultListed: false
			},
			$entity: {
				putable: true
			}
		},
		'league': {
			type: 'string',
			nullabe: true,
			description: 'The league in which the team competes',
			$collection: {
				postable: true,
				postMandatory: false,
				filterable: true,
				sortable: true,
				defaultListed: false
			},
			$entity: {
				putable: true
			}
		},
		'stadiumCapacity': {
			type: 'integer',
			nullabe: true,
			description: 'The capacity of the team\'s stadium',
			$collection: {
				postable: true,
				postMandatory: false,
				filterable: true,
				sortable: true,
				defaultListed: false
			},
			$entity: {
				putable: true
			}
		},
		'formationYear': {
			type: 'integer',
			nullabe: true,
			description: 'The year tthe football club was formed',
			$collection: {
				postable: true,
				postMandatory: false,
				filterable: true,
				sortable: true,
				defaultListed: false
			},
			$entity: {
				putable: true
			}
		}
	};
	// build list of fields that we'll pull back at each query...
	var fields = {
		'_id': 1
	};
	var collectionPropertyOptions = {};
	var entityPropertyOptions = {};
	for (var pty in baseProperties) {
		if (baseProperties.hasOwnProperty(pty)) {
			fields[pty] = 1;
			var propertyDef = baseProperties[pty];
			collectionPropertyOptions[pty] = {
				type: propertyDef.type,
				nullable: propertyDef.nullabe,
				description: propertyDef.description,
				postable: propertyDef.$collection.postable,
				postMandatory: propertyDef.$collection.postMandatory,
				filterable: propertyDef.$collection.filterable,
				sortable: propertyDef.$collection.sortable,
				defaultListed: propertyDef.$collection.defaultListed
			};
			entityPropertyOptions[pty] = {
				type: propertyDef.type,
				nullable: propertyDef.nullabe,
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
			dbobj['$ref'] = uri + '/' + mongo.getObjectIdString(dbobj);
			dbobj['id'] = mongo.getObjectIdString(dbobj);
			dbobj.remove('_id');
			return dbobj;
		}
	};
}