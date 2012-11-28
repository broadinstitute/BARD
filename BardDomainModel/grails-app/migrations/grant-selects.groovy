databaseChangeLog = {

    changeSet(author: "ddurkin", id: "grant select", dbms: 'oracle', context:'grant-selects',runAlways: 'true') {
        grailsChange {
            change {
                List<String> usernames = ['SCHATWIN', 'BARD_DEV', 'SBRUDZ', 'DATA_MIG', 'YCRUZ', 'SOUTHERN', 'BARD_QA', 'BARD_CI', 'DSTONICH', 'BALEXAND', 'DDURKIN', 'JASIEDU']
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



