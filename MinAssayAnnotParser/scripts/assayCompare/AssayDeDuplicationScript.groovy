import bard.db.registration.Assay
import bard.dm.Log
import org.apache.log4j.Level
import bard.dm.assaycompare.AssayMatch
import bard.dm.assaycompare.assaycleanup.DuplicateWriter
import bard.dm.assaycompare.assaycleanup.AssayContextAndItemDuplicateFinder
import bard.db.registration.AssayContext
import bard.dm.assaycompare.assaycleanup.AssayCleaner
import bard.dm.assaycompare.AssayDuplicateFinder

Log.initializeLogger("test/exampleData/logsAndOutput/assayDeDuplication.log")
Log.logger.setLevel(Level.DEBUG)

//load assays from database
Log.logger.info("begin loading assays")
final Date startLoadDate = new Date()
List<Assay> assayList = Assay.findAll()

final Date endLoadDate = new Date()
final loadTime = (endLoadDate.time - startLoadDate.time) / 60000.0
Log.logger.info("loaded ${assayList.size()} assays. load time[min]: ${loadTime} finished load: ${endLoadDate}")


Log.logger.info("Clean up duplicate assay context items with assay contexts, and assay contexts within assays")

DuplicateWriter duplicateWriter = new DuplicateWriter("test/exampleData/logsAndOutput")
AssayContextAndItemDuplicateFinder assayContextAndItemDuplicateFinder = new AssayContextAndItemDuplicateFinder()
AssayCleaner assayCleaner = new AssayCleaner()

List<AssayMatch> assayMatchList = new ArrayList<AssayMatch>(assayList.size())

AssayContext.withTransaction {status ->
    int count = 0
    for (Assay assay : assayList) {
        AssayMatch assayMatch = assayContextAndItemDuplicateFinder.removeDuplicateContextsAndItems(assay)
        assayMatchList.add(assayMatch)

        duplicateWriter.write(assayMatch)

        assayCleaner.clean(assayMatch)

        count++
        Log.logger.info("assay " + assay.getId() + " cleanup count: " + count + "  duplicates: " + assayMatch.duplicateOriginalContextMap.keySet().size()
                        + " contexts ")
    }
    duplicateWriter.closeAll()

    //uncomment line below to cause all changes to be rolled back
//    status.setRollbackOnly()
}

//run de-duplication
//AssayDuplicateFinder assayDuplicateFinder = new AssayDuplicateFinder("test/exampleData/assay_duplication_stats.csv")
//assayDuplicateFinder.findDuplicates(assayMatchList)

Log.close()

