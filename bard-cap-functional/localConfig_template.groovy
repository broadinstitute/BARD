mockUsers {
	integrationTestUser {
		roles = ['ROLE_USER', 'ROLE_CURATOR', 'ROLE_BARD_ADMINISTRATOR']
		username = 'someUser'
		password = 'password'
		email = 'someUser@nowhere.com'
	}
}
server.url=System.properties.getProperty('server.url') ?: "http://localhost:8080/BARD/"
