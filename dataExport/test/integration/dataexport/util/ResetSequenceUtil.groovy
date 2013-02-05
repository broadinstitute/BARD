package dataexport.util

import groovy.sql.Sql

import javax.sql.DataSource

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/28/12
 * Time: 11:35 PM
 * To change this template use File | Settings | File Templates.
 */
class ResetSequenceUtil {

    final DataSource dataSource

    ResetSequenceUtil(DataSource dataSource) {
        this.dataSource = dataSource
    }

    public void resetAllSequences() {
        Sql sql = new Sql(dataSource)
        def sequenceNames = []
        sql.eachRow('''select SEQUENCE_NAME from user_sequences''') {row ->
            sequenceNames.add(row.SEQUENCE_NAME)
        }
        resetSequences(sequenceNames)
    }

    public void resetSequences(List<String> sequenceNames) {
        for (String sequenceName in sequenceNames) {
            resetSequence(sequenceName)
        }
    }

    public void resetSequence(String sequenceName) {
        Sql sql = new Sql(dataSource)
        String tableName = sequenceName - '_ID_SEQ'
        String maxIdQuery = "select max(${tableName}_ID) as max_id from ${tableName}"
        try {
            int maxId = sql.rows(maxIdQuery)[0].max_id ?: 0

            String dropSeqSql = "drop sequence ${sequenceName}"
            //println(dropSeqSql)
            sql.execute(dropSeqSql)

            String alterSeqSql = "create sequence ${sequenceName} increment by 1 start with ${maxId + 1} maxvalue 2147483648 cache 2"
            //println(alterSeqSql)
            sql.execute(alterSeqSql)
            println("reset sequence ${sequenceName} to start with ${maxId + 1}")
        }
        catch (java.sql.SQLSyntaxErrorException e) {
            //println(e.message)
        }
        finally {
            sql.close()
        }
    }


}
