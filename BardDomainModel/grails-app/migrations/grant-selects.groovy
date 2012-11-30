databaseChangeLog = {

    changeSet(author: "ddurkin", id: "grant select", dbms: 'oracle', context:'grant-selects,standard',runAlways: 'true') {
        grailsChange {
            change {

                List<String> usernames = ['BALEXAND','BAMBOO','BARD_BAMBOO_CAP','BARD_BAMBOO_DATA_EXPORT',
                                          'BARD_BAMBOO_DOMAIN_MODEL','BARD_BAMBOO_QUERY_API','BARD_CAP_CHROME',
                                          'BARD_CAP_FIREFOX','BARD_CI','BARD_DEV','BARD_PROD','BARD_QA',
                                          'BARD_QA1','BARD_QA_CAP','DDURKIN','DLAHR_BARD','DSTONICH',
                                          'GWALZER_BARD','JASIEDU','JLEV','SBRUDZ','SCHATWIN','SOUTHERN',
                                          'XIAORONG','YCRUZ']

                String currentUsername = application?.config?.dataSource?.username?.toUpperCase()

                def tablenames = sql.rows('''select table_name from user_tables order by table_name''').collect{it.TABLE_NAME}

                def result = sql.withBatch(100){stmt ->
                    for(tablename in tablenames){
                        try{
                            for (username in usernames) {
                                if( username != currentUsername ){
                                    String grant = "GRANT SELECT ON ${tablename} TO ${username}"
                                    //println(grant)
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



