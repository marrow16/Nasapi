load("nashorn:mozilla_compat.js");
importPackage(com.mongodb);
(function() {
	console.log('INITIAL POPULATION OF TEAMS...');
	var initialTeams = [
		{
			'name': 'Arsenal',
			'city': 'London (North)',
			'owner': 'Arsenal Holdings plc',
			'league': 'Premier League',
			'stadiumCapacity': 60432,
			'formationYear': 1886
		},
		{
			'name': 'AFC Bournemouth',
			'city': 'Bournemouth',
			'owner': 'Maxim Demin',
			'league': 'Premier League',
			'stadiumCapacity': 11464,
			'formationYear': 1899
		},
		{
			'name': 'Brighton & Hove Albion',
			'city': 'Brighton',
			'owner': 'Tony Bloom',
			'league': 'Premier League',
			'stadiumCapacity': 30750,
			'formationYear': 1901
		},
		{
			'name': 'Burnley',
			'city': 'Burnley',
			'owner': 'Mike Garlick',
			'league': 'Premier League',
			'stadiumCapacity': 21800,
			'formationYear': 1882
		},
		{
			'name': 'Chelsea',
			'city': 'London (West)',
			'owner': 'Roman Abramovich',
			'league': 'Premier League',
			'stadiumCapacity': 41631,
			'formationYear': 1905
		},
		{
			'name': 'Crystal Palace',
			'city': 'London (South)',
			'owner': 'Steve Parish, Joshua Harris & David S. Blitzer',
			'league': 'Premier League',
			'stadiumCapacity': 25456,
			'formationYear': 1905
		},
		{
			'name': 'Everton',
			'city': 'Liverpool',
			'owner': 'Farhad Moshiri, Bill Kenwright & Jon Woods',
			'league': 'Premier League',
			'stadiumCapacity': 39572,
			'formationYear': 1878
		},
		{
			'name': 'Huddersfield Town',
			'city': 'Huddersfield',
			'owner': 'Dean Hoyle',
			'league': 'Premier League',
			'stadiumCapacity': 24500,
			'formationYear': 1908
		},
		{
			'name': 'Leicester City',
			'city': 'Leicester',
			'owner': 'King Power International Group',
			'league': 'Premier League',
			'stadiumCapacity': 32315,
			'formationYear': 1884
		},
		{
			'name': 'Liverpool',
			'city': 'Liverpool',
			'owner': 'Fenway Sports Group',
			'league': 'Premier League',
			'stadiumCapacity': 54074,
			'formationYear': 1892
		},
		{
			'name': 'Manchester City',
			'city': 'Manchester',
			'owner': 'City Football Group',
			'league': 'Premier League',
			'stadiumCapacity': 55097,
			'formationYear': 1880
		},
		{
			'name': 'Manchester United',
			'city': 'Manchester',
			'owner': 'Manchester United plc',
			'league': 'Premier League',
			'stadiumCapacity': 75643,
			'formationYear': 1878
		},
		{
			'name': 'Newcastle United',
			'city': 'Newcastle',
			'owner': 'Mike Ashley',
			'league': 'Premier League',
			'stadiumCapacity': 52534,
			'formationYear': 1892
		},
		{
			'name': 'Southampton',
			'city': 'Southampton',
			'owner': 'Katharina Liebherr',
			'league': 'Premier League',
			'stadiumCapacity': 32505,
			'formationYear': 1885
		},
		{
			'name': 'Stoke City',
			'city': 'Stoke-on-Trent',
			'owner': 'bet365',
			'league': 'Premier League',
			'stadiumCapacity': 27902,
			'formationYear': 1863
		},
		{
			'name': 'Swansea City',
			'city': 'Swansea',
			'owner': 'Jason Levien & Steve Kaplan',
			'league': 'Premier League',
			'stadiumCapacity': 21088,
			'formationYear': 1912
		},
		{
			'name': 'Tottenham Hotspur',
			'city': 'London (North)',
			'owner': 'ENIC International Ltd.',
			'league': 'Premier League',
			'stadiumCapacity': 90000,
			'formationYear': 1882
		},
		{
			'name': 'Watford',
			'city': 'Watford',
			'owner': 'Gino Pozzo',
			'league': 'Premier League',
			'stadiumCapacity': 21438,
			'formationYear': 1881
		},
		{
			'name': 'West Bromwich Albion',
			'city': 'West Bromwich',
			'owner': 'Guochuan Lai',
			'league': 'Premier League',
			'stadiumCapacity': 26852,
			'formationYear': 1878
		},
		{
			'name': 'West Ham United',
			'city': 'London (East)',
			'owner': 'David Sullivan & David Gold',
			'league': 'Premier League',
			'stadiumCapacity': 60000,
			'formationYear': 1895
		}
	];
	var collection = mongo.getCollection('teams');
	for (var i = 0, imax = initialTeams.length; i < imax; i++) {
		var team = initialTeams[i];
		console.log('Creating team: ', team.name);
		var dbobj = new com.mongodb.BasicDBObject();
		for (var pty in team) {
			if (team.hasOwnProperty(pty)) {
				dbobj.put(pty, team[pty]);
			}
		}
		dbobj.put('etag', java.util.UUID.randomUUID().toString());
		collection.save(dbobj);
	}
})();
