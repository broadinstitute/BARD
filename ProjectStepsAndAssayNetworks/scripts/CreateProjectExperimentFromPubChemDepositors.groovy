import org.apache.log4j.Level
import swamidass.clustering.Log
import swamidass.clustering.ParseSwamidassProjectsService
import org.springframework.transaction.support.DefaultTransactionStatus
import bard.db.project.Project

Log.logger.setLevel(Level.INFO)
ParseSwamidassProjectsService parseSwamidassProjectsService = ctx.getBean("parseSwamidassProjectsService")
assert parseSwamidassProjectsService

String path = 'C:/BARD_DATA_MIGRATION'
String filename = 'PubChemSummaryAIDs.txt'
def rootFolder = new File(path)
File infile = new File(path, filename)

Project.withTransaction {DefaultTransactionStatus status ->

//Update existing projects in CAP with new depositors pubchemAIDs (experiments).
//    parseSwamidassProjectsService.updateExistingProjectFromDepositorNeighbor()
//Create new projects (and their experiments) from the PubChem Summary AID file.
    parseSwamidassProjectsService.createNewProjectFromPubChemSummaryAIDs(infile)

    //comment out to commit the transaction
    status.setRollbackOnly()
}