import groovy.sql.Sql

def sql = new Sql(ctx.dataSource)
def application = grailsApplication

List<String> schemaNames = application?.config?.migrations?.grantSelects?.schemas
String currentUsername = application?.config?.dataSource?.username

def tablenames = sql.rows('''select table_name from user_tables order by table_name''').collect{it.TABLE_NAME}

def result = sql.withBatch(100){stmt ->
    for(tablename in tablenames){
        try{
            for (schemaName in schemaNames) {
                if( schemaName.toUpperCase() != currentUsername.toUpperCase() ){
                    String grant = "GRANT SELECT ON ${tablename} TO ${schemaName}"
                   // println(grant)
                    stmt.addBatch(grant)
                }
            }
       }
       catch(java.sql.SQLSyntaxErrorException e){
           println(e.message)
       }
    }
}

println(result.size())


