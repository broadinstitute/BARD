mockUsers {
	integrationTestUser {
		roles = ['ROLE_USER', 'ROLE_CURATOR', 'ROLE_BARD_ADMINISTRATOR']
		username = 'mrafique'
		password = '12345678'
		email = 'mrafique@broadinstitute.org'
	}
}
server.url=System.properties.getProperty('server.url') ?: "http://localhost:8080/BARD/"

//dbInfoMap=[url:"changeme", username:"changeme", password:"changeme", driver:"oracle.jdbc.OracleDriver"]
dbInfoMap=[url:"jdbc:oracle:thin:@barddev:1521:barddev", username:"bard_qa_cap", password:"Ze3eqe2T", driver:"oracle.jdbc.OracleDriver"]
//dbInfoMap=[url:"jdbc:oracle:thin:@barddev:1521:barddev", username:"bard_dev", password:"prEWr9safra8ahu", driver:"oracle.jdbc.OracleDriver"]