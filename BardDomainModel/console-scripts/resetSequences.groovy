import groovy.sql.Sql

def sql = new Sql(ctx.dataSource)
def sequenceNames = []
sql.eachRow('''select SEQUENCE_NAME from user_sequences'''){row ->
    sequenceNames.add(row.SEQUENCE_NAME)    
}
def potentialTableNames = sequenceNames.collect{ it - '_ID_SEQ' }



for(String sequenceName in sequenceNames){
   String tableName = sequenceName - '_ID_SEQ'
   String maxIdQuery = "select max(${tableName}_ID) as max_id from ${tableName}"    
   try{
     int maxId = sql.rows(maxIdQuery)[0].max_id ?: 0
         
     String dropSeqSql = "drop sequence ${sequenceName}"
     sql.execute(dropSeqSql)
     
     String alterSeqSql = "create sequence ${sequenceName} increment by 1 start with ${maxId + 1} maxvalue 2147483648 cache 2"
     sql.execute(alterSeqSql)
     println("reset sequence ${sequenceName} to start with ${maxId + 1}")
   }
   catch(java.sql.SQLSyntaxErrorException e){
       //println(e.message)
   }
         
}