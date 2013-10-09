import bard.db.registration.AssayDefinitionService
import groovy.sql.Sql
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

def sql = new Sql(ctx.dataSource)
AssayDefinitionService assayDefinitionService = ctx.assayDefinitionService

try {
    SpringSecurityUtils.reauthenticate('integrationTestUser', null)

    List<String> updateStatements = assayDefinitionService.loadNCGCAssayIds()
    if (updateStatements) {
        sql.withTransaction {
            def updateCounts = sql.withBatch(20) { stmt ->
                for (String updateStatement : updateStatements) {
                    stmt.addBatch(updateStatement)
                }
            }
            println "${updateCounts} Assays updated"
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