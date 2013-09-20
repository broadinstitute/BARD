import bard.db.experiment.ExperimentService
import groovy.sql.Sql
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

def sql = new Sql(ctx.dataSource)
ExperimentService experimentService = ctx.experimentService

try {
    SpringSecurityUtils.reauthenticate('integrationTestUser', null)

    List<String> updateStatements = experimentService.loadNCGCExperimentIds()
    if (updateStatements) {
        sql.withTransaction {
            def updateCounts = sql.withBatch(20) { stmt ->
                for (String updateStatement : updateStatements) {
                    stmt.addBatch(updateStatement)
                }
            }
            println "${updateCounts} Experiments updated"
        }
    }

}
catch (Exception e) {
    println(e)
   // tx.rollback()
}
finally {
   // tx.commit();
    // tx.rollback()
}