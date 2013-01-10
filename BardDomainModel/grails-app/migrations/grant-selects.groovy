databaseChangeLog = {

    /**
     * script to grant select priv for all tables in a schema to all the users
     */
    changeSet(author: "ddurkin", id: "grant select", dbms: 'oracle', context:'grant-selects',runAlways: 'true') {
        grailsChange {
            change {
                /* so in an exerternalized config file specify
                 * migrations.grantSelects.schemas = ['FOO','BAR']
                 * where the schemas that should be granted select privs are enumerated
                 */
                def schemaNames = application?.config?.migrations?.grantSelects?.schemas
                String currentUsername = application?.config?.dataSource?.username

                def tablenames = sql.rows('''select table_name from user_tables order by table_name''').collect{it.TABLE_NAME}

                def result = sql.withBatch(100){stmt ->
                    for(tablename in tablenames){
                        try{
                            for (String schemaName in schemaNames) {
                                // can't grant select to yourself in oracle
                                if( schemaName.toUpperCase() != currentUsername.toUpperCase() ){
                                    String grant = "GRANT SELECT ON ${tablename} TO ${schemaName}"
//                                    println(grant)
                                    stmt.addBatch(grant)
                                }
                            }
                       }
                       catch(java.sql.SQLSyntaxErrorException e){
                           println(e.message)
                       }
                    }
                }
            }
        }
    }

}



