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

 