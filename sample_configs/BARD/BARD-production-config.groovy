databaseHost = "DATABASE_HOST"
databaseSid = "DATABASE_SID"
dataSource {
    url = 'jdbc:oracle:thin:@${databaseHost}:1521:${databaseSid}'
    driverClassName = 'oracle.jdbc.driver.OracleDriver'
    dialect = 'bard.SequencePerTableOracleDialect'
    username = "USERNAME"
    password = "PASSWORD"
    dbCreate='no-val'
}

CbipCrowd {
    mockUsers.put('admin', [roles : ['ROLE_USER', 'ROLE_BARD_ADMINISTRATOR','ROLE_ADMIN'], username : 'admin', password : 'test', email:'test@nowhere.com'])
}

bard.services.resultService.archivePath = "RESULTS_PATH"
grails.jesque.enabled = true
